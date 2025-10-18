package ch.arcticsoft.whoop;

import java.time.Instant;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.result.view.Rendering;


import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
public class HomeController {

	private static final Logger log = LoggerFactory.getLogger(HomeController.class);
    private final InMemoryReactiveClientRegistrationRepository registrations;

	
	public HomeController(InMemoryReactiveClientRegistrationRepository registrations) {
	      this.registrations = registrations;
	}
	
    @GetMapping({"/", "/index"})
    public Mono<Rendering> home() {
        return Mono.just(Rendering.view("index").build());
    }
    
    @GetMapping({"/hello"})
    public Mono<Rendering> index() { 	
        Flux<String> regs = Flux.fromIterable(registrations).map(ClientRegistration::getRegistrationId);
        return Mono.just(
            Rendering.view("hello") // looks for templates/index.html
                     .modelAttribute("message", "Hello from WebFlux + Thymeleaf!")
                     .modelAttribute("clients", regs)                     
                     .build()
        );
    }

    
    @GetMapping("/whoop/profile")
    public Mono<Map<String, Object>> profile(
        @RegisteredOAuth2AuthorizedClient("whoop") OAuth2AuthorizedClient client,
        WebClient whoopWebClient
    ) {
      return whoopWebClient.get()
          .uri("/developer/v2/user/profile/basic")
          //.attributes(oauth2AuthorizedClient(client)) // attach this user’s token
          .retrieve()
          .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {});
    }
    

    
}
