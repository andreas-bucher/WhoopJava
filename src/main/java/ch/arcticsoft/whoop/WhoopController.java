package ch.arcticsoft.whoop;

import java.time.OffsetDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.result.view.Rendering;


import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
public class WhoopController {

	private static final Logger log = LoggerFactory.getLogger(WhoopController.class);
    private final InMemoryReactiveClientRegistrationRepository registrations;
    private final WhoopWorkoutService service;
	
	public WhoopController(
			InMemoryReactiveClientRegistrationRepository registrations,
			WhoopWorkoutService service) {
		
	      this.registrations = registrations;
	      this.service = service;
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

    /**
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
    }**/
    
    
    /** Return all workouts in a date range as a single JSON array (collects all pages). */
    @GetMapping("/workouts")
    public Mono<Rendering> workouts(
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime start,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime end,
        @RequestParam(required = false, defaultValue = "25") Integer limit
    ) {
    	log.info("workouts ...");
    	var allWorkouts = service.streamAllWorkouts(WhoopWorkoutService.iso(start), WhoopWorkoutService.iso(end), limit).collectList();
    	log.info("");
    	log.info( allWorkouts.toString() );
    	return Mono.just(
    		      Rendering.view("workouts")                 // looks for templates/workouts.html
    		               .modelAttribute("records", List.of(Map.of("id","abc","duration",1800)))
    		               .build()
    		    );
    }

    /** Stream workouts as Server-Sent Events (handy for big ranges). */
    @GetMapping(value = "/workouts/stream", produces = "text/event-stream")
    public Flux<Map<String, Object>> workoutsStream(
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime start,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime end,
        @RequestParam(required = false, defaultValue = "25") Integer limit
    ) {
      return service.streamAllWorkouts(WhoopWorkoutService.iso(start), WhoopWorkoutService.iso(end), limit);
    }
    
    
    @GetMapping("/calendar")
    public String index(Model model) {
        // Example: pass today’s date to the view (not strictly needed by FullCalendar)
        model.addAttribute("today", LocalDate.now());
        return "calendar";
    }
    
}
