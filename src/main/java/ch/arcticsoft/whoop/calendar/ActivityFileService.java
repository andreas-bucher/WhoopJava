package ch.arcticsoft.whoop.calendar;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.arcticsoft.whoop.WhoopWorkoutService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ActivityFileService {
	
	private static final Logger log = LoggerFactory.getLogger(ActivityFileService.class);

    @Value("${whoop.activities.path}")
    private String activitiesFilePath;

    private final ObjectMapper objectMapper;

    private List<Activity> activities = Collections.emptyList();
    private Map<LocalDate, List<Activity>> activitiesByDate = Collections.emptyMap();


    public ActivityFileService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void loadActivities() throws IOException {
    	log.info("loadActivities: "+activitiesFilePath);
        File file = new File(activitiesFilePath);
        if (!file.exists()) {
            throw new IOException("Activities file not found: " + activitiesFilePath);
        }

        if( this.activities.size() > 0 ) {
        	log.info(" ... activities already loaded");
        	return;
        }
        
        this.activities = objectMapper.readValue(
                file,
                new TypeReference<List<Activity>>() {}
        );
        
        // Build index: LocalDate -> Activities of that day
        this.activitiesByDate = activities.stream()
                .filter(a -> a.getStart() != null && a.getTimezoneOffset() != null)
                .collect(Collectors.groupingBy(this::extractLocalDateFromActivity));
        
    }

    public List<Activity> getAllActivities() {
        return Collections.unmodifiableList(activities);
    }

    public Optional<Activity> getById(String id) {
        return activities.stream()
                .filter(a -> a.getId().equals(id))
                .findFirst();
    }

    public List<Activity> findBySportName(String sportName) {
        return activities.stream()
                .filter(a -> sportName.equalsIgnoreCase(a.getSportName()))
                .collect(Collectors.toList());
    }

    public List<Activity> findByUserId(long userId) {
        return activities.stream()
                .filter(a -> a.getUserId() == userId)
                .collect(Collectors.toList());
    }
    /**
     * Get activities for a given LocalDate (in the activity's own timezone_offset).
     */
    public List<Activity> getActivitiesOnDate(LocalDate date) {
        return activitiesByDate.getOrDefault(date, Collections.emptyList());
    }

    /**
     * Convenience method: date as ISO string "YYYY-MM-DD".
     */
    public List<Activity> getActivitiesOnDate(String isoDate) {
        LocalDate date = LocalDate.parse(isoDate);
        return getActivitiesOnDate(date);
    }

    /**
     * Get all activities whose (start) local date is in [from, to] inclusive.
     */
    public List<Activity> getActivitiesBetween(LocalDate from, LocalDate to) {
        if (from == null || to == null) {
            return Collections.emptyList();
        }

        log.trace("from "+from.toString());
        log.trace("to "+to.toString());
        

        List<Activity> result = new ArrayList<>();

        for (LocalDate d = from; !d.isAfter(to); d = d.plusDays(1)) {
            List<Activity> dayActivities = activitiesByDate.get(d);
            if (dayActivities != null && !dayActivities.isEmpty()) {
                result.addAll(dayActivities);
            }
        }
    	log.debug("ActivitiesBetween.size : "+Integer.toString(result.size()));
        return result;
    }

    // ===== helper methods =====

    private LocalDate extractLocalDateFromActivity(Activity activity) {
        ZoneOffset offset = ZoneOffset.of(activity.getTimezoneOffset());
        OffsetDateTime odt = activity.getStart().atOffset(offset);
        return odt.toLocalDate();
    }
}
