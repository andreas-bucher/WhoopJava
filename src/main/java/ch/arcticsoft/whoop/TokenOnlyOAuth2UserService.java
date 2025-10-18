package ch.arcticsoft.whoop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//TokenOnlyOAuth2UserService.java
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.ReactiveOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import java.util.List;
import java.util.Map;

@Component
public class TokenOnlyOAuth2UserService
 	implements ReactiveOAuth2UserService<OAuth2UserRequest, OAuth2User> {

	private static final Logger log = LoggerFactory.getLogger(TokenOnlyOAuth2UserService.class);
	
	@Override
	public Mono<OAuth2User> loadUser(OAuth2UserRequest req) {
		log.info("loadUser ...");
		// Build a tiny local principal; avoids any UserInfo call
		String principal = req.getClientRegistration().getRegistrationId()
		     + ":" + req.getAccessToken().getTokenValue().hashCode();
		Map<String,Object> attrs = Map.of("principal", principal);
		return Mono.just(new DefaultOAuth2User(
		     List.of(new SimpleGrantedAuthority("ROLE_USER")), attrs, "principal"));
	}
}