package ch.arcticsoft.whoop.calendar;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Activity {

    private String id;

    @JsonProperty("v1_id")
    private long v1Id;

    @JsonProperty("user_id")
    private long userId;

    @JsonProperty("created_at")
    private Instant createdAt;

    @JsonProperty("updated_at")
    private Instant updatedAt;

    private Instant start;
    private Instant end;

    @JsonProperty("timezone_offset")
    private String timezoneOffset;

    @JsonProperty("sport_name")
    private String sportName;

    @JsonProperty("score_state")
    private String scoreState;

    private Score score;

    @JsonProperty("sport_id")
    private int sportId;

    // getters and setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getV1Id() {
        return v1Id;
    }

    public void setV1Id(long v1Id) {
        this.v1Id = v1Id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Instant getStart() {
        return start;
    }
    
    public LocalDateTime getStart2() {
    	ZoneOffset offset = ZoneOffset.of(timezoneOffset);
    	LocalDateTime ldt = LocalDateTime.ofInstant(start, offset);
		return ldt;
    }

    public void setStart(Instant start) {
        this.start = start;
    }

    public Instant getEnd() {
        return end;
    }
    
    public LocalDateTime getEnd2() {
    	ZoneOffset offset = ZoneOffset.of(timezoneOffset);
    	LocalDateTime ldt = LocalDateTime.ofInstant(end, offset);
		return ldt;    	
    }

    public void setEnd(Instant end) {
        this.end = end;
    }

    public String getTimezoneOffset() {
        return timezoneOffset;
    }

    public void setTimezoneOffset(String timezoneOffset) {
        this.timezoneOffset = timezoneOffset;
    }

    public String getSportName() {
        return sportName;
    }

    public void setSportName(String sportName) {
        this.sportName = sportName;
    }

    public String getScoreState() {
        return scoreState;
    }

    public void setScoreState(String scoreState) {
        this.scoreState = scoreState;
    }

    public Score getScore() {
        return score;
    }

    public void setScore(Score score) {
        this.score = score;
    }

    public int getSportId() {
        return sportId;
    }

    public void setSportId(int sportId) {
        this.sportId = sportId;
    }
}
