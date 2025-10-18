package ch.arcticsoft.whoop;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationFailureHandler;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;


@Configuration
public class OAuth2FailureHandlers {

  private static final Logger log = LoggerFactory.getLogger(OAuth2FailureHandlers.class);
	
  @Bean
  ServerAuthenticationFailureHandler oauthFailureHandler() {
	log.info("oauthFailureHandler ...");
    var delegate = new RedirectServerAuthenticationFailureHandler("/login?oauthError");
    return (WebFilterExchange web, AuthenticationException ex) -> {
      log.error("OAuth2 login failed: {}", ex.getMessage(), ex);
      Throwable root = rootCause(ex);
      if (root instanceof WebClientResponseException wcre) {
        log.error("Token/UserInfo HTTP {} {} — response body:\n{}",
            wcre.getStatusCode(), wcre.getStatusText(), wcre.getResponseBodyAsString());
      }
      String msg = ex.getMessage();

      return web.getExchange().getSession()
          .doOnNext(s -> s.getAttributes().put("oauthError", msg))
          .then(delegate.onAuthenticationFailure(web, ex));
    };
  }

  private static Throwable rootCause(Throwable t) {
    Throwable r = t;
    while (r.getCause() != null && r.getCause() != r) r = r.getCause();
    return r;
  }
	
}
