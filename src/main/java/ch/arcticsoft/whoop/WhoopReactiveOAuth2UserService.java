package ch.arcticsoft.whoop;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.ReactiveOAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;


/**
@Component
public class WhoopReactiveOAuth2UserService implements ReactiveOAuth2UserService<OAuth2UserRequest, OAuth2User>{

	
	private static final Logger log = LoggerFactory.getLogger(WhoopReactiveOAuth2UserService.class);
	
	private final WebClient web = WebClient.builder().baseUrl("https://api.prod.whoop.com").build();
	
	@Override
	public Mono<OAuth2User> loadUser(OAuth2UserRequest req) throws OAuth2AuthenticationException {
	    var token = req.getAccessToken().getTokenValue();

	    return web.get()
	      .uri("/developer/v2/user/profile/basic")
	      .headers(h -> h.setBearerAuth(token))
	      .accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_OCTET_STREAM)
	      .retrieve()
	      .bodyToMono(byte[].class)               // tolerate wrong content-type
	      .flatMap(bytes -> {
	        try {
	          @SuppressWarnings("unchecked")
	          Map<String,Object> attrs = new ObjectMapper().readValue(bytes, Map.class);
	          String nameAttr = attrs.containsKey("user_id") ? "user_id" : "id";
	          return Mono.just(new DefaultOAuth2User(
	              List.of(new SimpleGrantedAuthority("ROLE_USER")),
	              attrs, nameAttr));
	        } catch (Exception e) {
	          return Mono.error(e);
	        }
	      });
	}

}


/**
@Component
public class TokenOnlyOAuth2UserService
    implements ReactiveOAuth2UserService<OAuth2UserRequest, OAuth2User> {

  @Override
  public Mono<OAuth2User> loadUser(OAuth2UserRequest req) {
    Map<String,Object> attrs = Map.of(
        "client", req.getClientRegistration().getRegistrationId(),
        "principal", "whoop-user"  // placeholder; replace if you have a real id
    );
    return Mono.just(new DefaultOAuth2User(
        List.of(new SimpleGrantedAuthority("ROLE_USER")), attrs, "principal"));
  }
}
**/