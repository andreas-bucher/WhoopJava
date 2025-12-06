package ch.arcticsoft.whoop;

import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository;


import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import ch.arcticsoft.whoop.calendar.CalendarEvent;
import reactor.core.publisher.Mono;



@RestController
public class WhoopRestController {

	private static final Logger log = LoggerFactory.getLogger(WhoopRestController.class);
	private final WebClient webClient;
    private final InMemoryReactiveClientRegistrationRepository registrations;
    private final WhoopWorkoutService service;
    
    
    private final List<CalendarEvent> events = new CopyOnWriteArrayList<>();
    private final AtomicLong idGen = new AtomicLong(1);
    
/**
	public WhoopRestController(WebClient webClient) {
		log.info("HomeRestController");
		this.webClient = webClient;
	}
**/
	public WhoopRestController(
			WebClient webClient,
			InMemoryReactiveClientRegistrationRepository registrations,
			WhoopWorkoutService service) {
		log.info("WhoopRestController ... ");		
		this.webClient = webClient;		
	    this.registrations = registrations;
	    this.service = service;
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
    
    /**
     * curl -s "https://api.prod.whoop.com/developer/v2/activity/workout" -H "Authorization: Bearer $TOKEN"
     * @return
     */
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

    /** Return all workouts in a date range as a single JSON array (collects all pages). */
    @GetMapping("/api/workouts")
    public Mono<java.util.List<Map<String, Object>>> workouts(
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime start,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime end,
        @RequestParam(required = false, defaultValue = "25") Integer limit
    ) {
    	log.info("workouts ...");
    	var allWorkouts = service.streamAllWorkouts(WhoopWorkoutService.iso(start), WhoopWorkoutService.iso(end), limit).collectList();
    	log.info("");
    	log.info( allWorkouts.toString() );
    	return allWorkouts;
    }
    
    @GetMapping("/api/workouts-save")
    public Mono<String> saveWorkoutsToFile(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime end,
            @RequestParam(required = false, defaultValue = "25") Integer limit	
    	) {
    	log.info("saveWorkoutsToFile ...");
    	var allWorkouts = service.streamAllWorkouts(WhoopWorkoutService.iso(start), WhoopWorkoutService.iso(end), limit).collectList();
      return allWorkouts
    		  .flatMap(records -> writeToFile(records, "whoop-workouts-2023-01.json"))
    		  .thenReturn("✅ Workouts saved to file successfully");
    }   
    private Mono<Void> writeToFile(List<Map<String, Object>> records, String fileName) {
        return Mono.fromRunnable(() -> {
          try {
            Path file = Path.of(System.getProperty("user.home"), fileName);
			ObjectMapper mapper = new ObjectMapper();
			ObjectWriter objectWriter = mapper.writerWithDefaultPrettyPrinter();
			objectWriter.writeValue(file.toFile(), records);
            log.info("Saved JSON → " + file);
          } catch (Exception e) {
            throw new RuntimeException("Failed to write JSON", e);
          }
        });
      }
    
    
    /**
     * curl -s "https://api.prod.whoop.com/developer/v2/activity/workout/0473abc1-a268-4b14-811f-7940f7ce6198" -H "Authorization: Bearer G0e0ycRyBXfvTkP03VOT4DzOyMyc7Gy07fSGI7QW_3s.ORTViXuKqzn8rEg48hnoHPZBrxvDE_QVeD5xwnsnDUU"
	*/    
    @GetMapping("/api/workout/{id}")
    public Mono<Map<String, Object>> getWorkout(@PathVariable String id) {
      return service.getWorkoutDetail(id);
    }    

    
    
    @GetMapping("/api/events")
    public List<CalendarEvent> getEvents() {
    	log.info("getEvents ...");
        events.add(new CalendarEvent(
                idGen.getAndIncrement(),
                "Initial demo event",
                LocalDateTime.now().withHour(10).withMinute(0).withSecond(0).withNano(0),
                LocalDateTime.now().withHour(11).withMinute(0).withSecond(0).withNano(0)
        ));
    	return events;
    }
    
 /**
    @PostMapping
    public CalendarEvent createEvent(@RequestBody CalendarEvent event) {
        event.setId(idGen.getAndIncrement());
        // if no end is given, create a 1h event
        if (event.getEnd() == null && event.getStart() != null) {
            event.setEnd(event.getStart().plusHours(1));
        }
        events.add(event);
        return event;
    }
 **/
    
}
