package ch.arcticsoft.whoop;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

public class InMemoryTrackingAuthorizedClientRepository implements ServerOAuth2AuthorizedClientRepository {

	private static final Logger log = LoggerFactory.getLogger(InMemoryTrackingAuthorizedClientRepository.class);
	private final Map<String, OAuth2AuthorizedClient> authorizedClients = new ConcurrentHashMap<>();

	@Override
	public <T extends OAuth2AuthorizedClient> Mono<T> loadAuthorizedClient(
			String clientRegistrationId, Authentication principal, ServerWebExchange exchange) {
		log.info("loadAuthorizedClient .... >");
		log.info(clientRegistrationId);		
		return Mono.justOrEmpty((T) authorizedClients.get(principal.getName() + ":" + clientRegistrationId));
	}
	
	@Override
	public Mono<Void> saveAuthorizedClient(OAuth2AuthorizedClient authorizedClient,
	                                   Authentication principal,
	                                   ServerWebExchange exchange) {
		log.info("saveAuthorizedClient .... >");
		//log.info(authorizedClient.toString());
		log.info(authorizedClient.getPrincipalName());
		authorizedClients.put(principal.getName() + ":" + authorizedClient.getClientRegistration().getRegistrationId(),
		                      authorizedClient);
		return Mono.empty();
	}
	
	@Override
	public Mono<Void> removeAuthorizedClient(String clientRegistrationId,
	                                     Authentication principal,
	                                     ServerWebExchange exchange) {
		authorizedClients.remove(principal.getName() + ":" + clientRegistrationId);
		return Mono.empty();
	}
	
	public Collection<OAuth2AuthorizedClient> listAll() {
		return authorizedClients.values();
	}
}