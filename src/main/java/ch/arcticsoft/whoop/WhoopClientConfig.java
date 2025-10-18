package ch.arcticsoft.whoop;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;


@Configuration
public class WhoopClientConfig {

  private static final Logger log = LoggerFactory.getLogger(WhoopClientConfig.class);
	
  @Bean
  WebClient whoopWebClient(ReactiveClientRegistrationRepository registrations,
                           ServerOAuth2AuthorizedClientRepository authorizedClients) {
	log.info("whoopWebClient ...");
    var oauth = new ServerOAuth2AuthorizedClientExchangeFilterFunction(registrations, authorizedClients);
    oauth.setDefaultOAuth2AuthorizedClient(true);  // use the current user’s token automatically

    // Filter to log full URI and HTTP method
    ExchangeFilterFunction logRequest = ExchangeFilterFunction.ofRequestProcessor(request -> {
      log.info("️  WebClient Request: " + request.method() + " " + request.url());
      request.headers().forEach((name, values) ->
          log.info("   " + name + ": " + String.join(",", values)));
      return Mono.just(request);
    });

    ExchangeFilterFunction logResponse = ExchangeFilterFunction.ofResponseProcessor(response -> {
      log.info("  WebClient Response: " + response.statusCode());
      return Mono.just(response);
    });    
    
    
    return WebClient.builder()
        .baseUrl("https://api.prod.whoop.com")
        .filter(oauth)
        .filter(logRequest)
        .filter(logResponse)
        .build();
  }
}
