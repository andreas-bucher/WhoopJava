package ch.arcticsoft.whoop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2LoginReactiveAuthenticationManager;
import org.springframework.security.oauth2.client.endpoint.WebClientReactiveAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationFailureHandler;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

	private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);
	
	//private final WhoopReactiveOAuth2UserService whoopUserService;
	private final ServerAuthenticationFailureHandler oauthFailureHandler;
	private final TokenOnlyOAuth2UserService tokenOnlyUserService;	

	public SecurityConfig(ServerAuthenticationFailureHandler oauthFailureHandler,
			TokenOnlyOAuth2UserService tokenOnlyUserService) {
	    this.oauthFailureHandler = oauthFailureHandler;
	    this.tokenOnlyUserService = tokenOnlyUserService;
	    
	}
	
	  @Bean
	  OAuth2LoginReactiveAuthenticationManager oauth2AuthManager() {
	    var accessTokenClient =
	        new WebClientReactiveAuthorizationCodeTokenResponseClient(); // handles code -> token
	    return new OAuth2LoginReactiveAuthenticationManager(accessTokenClient, tokenOnlyUserService);
	  }
	
	@Bean
	SecurityWebFilterChain security(ServerHttpSecurity http,
									ServerAuthenticationFailureHandler oauthFailureHandler) {
	    log.info("security ...");
		return http
	      .authorizeExchange(ex -> ex
	        .pathMatchers("/", "/login**", "/error**").permitAll()
	        .anyExchange().authenticated())
	      .oauth2Login( o -> o.authenticationFailureHandler(oauthFailureHandler))
	      .oauth2Client( Customizer.withDefaults() )
	      .build();
	}
/**
	@Bean
	ServerAuthenticationFailureHandler failureHandler() {
		log.info("failureHandler ...");
		log.info("");
	    var delegate = new RedirectServerAuthenticationFailureHandler("/login?oauthError");
	    return (WebFilterExchange web, AuthenticationException ex) -> {
	      // 1) Log a useful summary
	        log.error("OAuth2 login failed: {}", ex.getMessage(), ex);

	      // 2) Put a compact message in session to show on /login
	      return web.getExchange().getSession()
	          .doOnNext(s -> s.getAttributes().put("oauthError", ex.getMessage()))
	          .then(delegate.onAuthenticationFailure(web, ex));
	    };
	}
	**/
}
