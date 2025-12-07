package ch.arcticsoft.whoop.calendar;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Score {
    private float strain;

    @JsonProperty("average_heart_rate")
    private Integer averageHeartRate;

    @JsonProperty("max_heart_rate")
    private Integer maxHeartRate;

    private Float kilojoule;

    @JsonProperty("percent_recorded")
    private Float percentRecorded;

    @JsonProperty("distance_meter")
    private Float distanceMeter;

    @JsonProperty("altitude_gain_meter")
    private Float altitudeGainMeter;

    @JsonProperty("altitude_change_meter")
    private Float altitudeChangeMeter;

    @JsonProperty("zone_durations")
    private ZoneDurations zoneDurations;

    // getters and setters

    public float getStrain() {
        return strain;
    }

    public void setStrain(float strain) {
        this.strain = strain;
    }

    public Integer getAverageHeartRate() {
        return averageHeartRate;
    }

    public void setAverageHeartRate(Integer averageHeartRate) {
        this.averageHeartRate = averageHeartRate;
    }

    public Integer getMaxHeartRate() {
        return maxHeartRate;
    }

    public void setMaxHeartRate(Integer maxHeartRate) {
        this.maxHeartRate = maxHeartRate;
    }

    public Float getKilojoule() {
        return kilojoule;
    }

    public void setKilojoule(Float kilojoule) {
        this.kilojoule = kilojoule;
    }

    public Float getPercentRecorded() {
        return percentRecorded;
    }

    public void setPercentRecorded(Float percentRecorded) {
        this.percentRecorded = percentRecorded;
    }

    public Float getDistanceMeter() {
        return distanceMeter;
    }

    public void setDistanceMeter(Float distanceMeter) {
        this.distanceMeter = distanceMeter;
    }

    public Float getAltitudeGainMeter() {
        return altitudeGainMeter;
    }

    public void setAltitudeGainMeter(Float altitudeGainMeter) {
        this.altitudeGainMeter = altitudeGainMeter;
    }

    public Float getAltitudeChangeMeter() {
        return altitudeChangeMeter;
    }

    public void setAltitudeChangeMeter(Float altitudeChangeMeter) {
        this.altitudeChangeMeter = altitudeChangeMeter;
    }

    public ZoneDurations getZoneDurations() {
        return zoneDurations;
    }

    public void setZoneDurations(ZoneDurations zoneDurations) {
        this.zoneDurations = zoneDurations;
    }
}
