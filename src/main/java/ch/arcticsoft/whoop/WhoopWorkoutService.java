package ch.arcticsoft.whoop;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@Service
public class WhoopWorkoutService {

	private static final Logger log = LoggerFactory.getLogger(WhoopWorkoutService.class);
	
	private final WebClient whoop;
	private final ObjectMapper mapper = new ObjectMapper();

	public WhoopWorkoutService(WebClient whoopWebClient) {
		log.info("WhoopWorkoutService");
	    this.whoop = whoopWebClient;
	}

	  /** Fetch one page (returns Map with keys: records, next_token) 
	   * 
	   * curl -s "https://api.prod.whoop.com/developer/v2/activity/workout" -H "Authorization: Bearer $TOKEN"
	   *         
	   * */
	  public Mono<Map<String, Object>> getWorkoutPage(String startIso, String endIso, Integer limit, String nextToken) {
		log.info("getWorkoutPage ... ");
	    var spec = whoop.get()
	        .uri(uri -> {
	          var b = uri.path("/developer/v2/activity/workout");
	          if (nextToken != null) b.queryParam("nextToken", nextToken);
	          else {
	            if (startIso != null) b.queryParam("start", startIso);
	            if (endIso != null)   b.queryParam("end",   endIso);
	            if (limit != null)    b.queryParam("limit", limit);
	          }
	          return b.build();
	        })
	        .accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_OCTET_STREAM);
	    // WHOOP sometimes sets octet-stream though the body is JSON; read bytes then parse.
	    return spec.retrieve()
	        .bodyToMono(byte[].class)
	        .map(bytes -> {
	          try {
	            @SuppressWarnings("unchecked")
	            Map<String, Object> m = mapper.readValue(bytes, Map.class);
	            return m;
	          } catch (Exception e) {
	            throw new RuntimeException("Failed to parse WHOOP workouts response", e);
	          }
	        });
	  }

	  /** Stream ALL workouts in range by following next_token (as Flux of workout maps). */
	  public Flux<Map<String, Object>> streamAllWorkouts(String startIso, String endIso, Integer pageSize) {
	    return getWorkoutPage(startIso, endIso, pageSize, null)
	        .expand(page -> {
	          String next = (String) page.get("next_token");
	          return (next == null) ? Mono.empty() : getWorkoutPage(null, null, null, next);
	        })
	        .flatMap(page -> {
	          @SuppressWarnings("unchecked")
	          List<Map<String, Object>> recs = (List<Map<String, Object>>) page.getOrDefault("records", List.of());
	          return Flux.fromIterable(recs);
	        });
	  }

	  /** Convenience helpers for ISO strings. */
	  public static String iso(OffsetDateTime odt) { return odt == null ? null : odt.toInstant().toString(); }

	  
	  /** Fetch details for a specific workout UUID */
	  public Mono<Map<String, Object>> getWorkoutDetail(String workoutId) {
	    return whoop.get()
	        .uri("/developer/v2/activity/workout/{id}", workoutId)
	        .accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_OCTET_STREAM)
	        .retrieve()
	        .onStatus(HttpStatusCode::isError, resp ->
	            resp.bodyToMono(String.class)
	                .flatMap(body -> Mono.error(
	                    new RuntimeException("WHOOP error " + resp.statusCode() + ": " + body))))
	        .bodyToMono(byte[].class)  // WHOOP sometimes marks JSON as octet-stream
	        .map(bytes -> {
	          try {
	            return mapper.readValue(bytes, Map.class);
	          } catch (Exception e) {
	            throw new RuntimeException("JSON parse error for WHOOP workout " + workoutId, e);
	          }
	        });
	  }
	  
}
