package ch.arcticsoft.whoop;

import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;

import java.time.Instant;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;



@RestController
public class WhoopRestController {

	private static final Logger log = LoggerFactory.getLogger(WhoopRestController.class);
	private final WebClient webClient;
	

	public WhoopRestController(WebClient webClient) {
		log.info("HomeRestController");
		this.webClient = webClient;
	}

	
	
    @GetMapping("/oauth/verify")
    Mono<Map<String, Object>> verify(@RegisteredOAuth2AuthorizedClient OAuth2AuthorizedClient client) {
      log.info("verify");
      var access = client.getAccessToken();
      var refresh = client.getRefreshToken();

      Map<String, Object> info = new LinkedHashMap<>();
      info.put("client", client.getClientRegistration().getRegistrationId());
      info.put("principal", client.getPrincipalName());
      info.put("access_token_present", access != null);
      info.put("access_token_expires_at", access != null ? access.getExpiresAt() : null);
      info.put("scopes", access != null ? access.getScopes() : List.of());
      info.put("refresh_token_present", refresh != null);

      // simple validity check
      info.put("is_access_token_valid_now",
        access != null && access.getExpiresAt() != null && access.getExpiresAt().isAfter(Instant.now()));
      log.info(client.getClientRegistration().getRegistrationId());
      return Mono.just(info);
    }   
    
    /**
    @GetMapping("/oauth/verifyWhoop")
    Mono<Map<String, Object>> verifyWhoop(@RegisteredOAuth2AuthorizedClient("whoop") OAuth2AuthorizedClient client) {
      log.info("verifyWhoop");
      var access = client.getAccessToken();
      var refresh = client.getRefreshToken();

      Map<String, Object> info = new LinkedHashMap<>();
      info.put("client", client.getClientRegistration().getRegistrationId());
      info.put("principal", client.getPrincipalName());
      info.put("access_token_present", access != null);
      info.put("access_token_expires_at", access != null ? access.getExpiresAt() : null);
      info.put("scopes", access != null ? access.getScopes() : List.of());
      info.put("refresh_token_present", refresh != null);

      // simple validity check
      info.put("is_access_token_valid_now",
        access != null && access.getExpiresAt() != null && access.getExpiresAt().isAfter(Instant.now()));
      log.info(client.getClientRegistration().getRegistrationId());
      return Mono.just(info);
    }     
    **/  
    

    @GetMapping("/whoop/recovery")
    public Mono<Map<String,Object>> recovery() {
      log.info("recovery");
      return webClient.get()
          .uri("/developer/v2/recovery")
          .retrieve()
          .bodyToMono(new ParameterizedTypeReference<>() {});
    }

    @GetMapping("/whoop/workout")
    public Mono<Map<String,Object>> workout() {
      log.info("recovery");
      return webClient.get()
          .uri("/developer/v2/activity/workout")
          .retrieve()
          .bodyToMono(new ParameterizedTypeReference<>() {});
    }
    
    @GetMapping("/token")
    public String token(@RegisteredOAuth2AuthorizedClient("whoop") OAuth2AuthorizedClient client) {
      log.info("token");
      return client.getAccessToken().getTokenValue();
    }

    
}
