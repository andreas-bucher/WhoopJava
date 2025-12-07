package ch.arcticsoft.whoop.calendar;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ZoneDurations {

    @JsonProperty("zone_zero_milli")
    private Long zoneZeroMilli;

    @JsonProperty("zone_one_milli")
    private Long zoneOneMilli;

    @JsonProperty("zone_two_milli")
    private Long zoneTwoMilli;

    @JsonProperty("zone_three_milli")
    private Long zoneThreeMilli;

    @JsonProperty("zone_four_milli")
    private Long zoneFourMilli;

    @JsonProperty("zone_five_milli")
    private Long zoneFiveMilli;

    // getters and setters

    public Long getZoneZeroMilli() {
        return zoneZeroMilli;
    }

    public void setZoneZeroMilli(Long zoneZeroMilli) {
        this.zoneZeroMilli = zoneZeroMilli;
    }

    public Long getZoneOneMilli() {
        return zoneOneMilli;
    }

    public void setZoneOneMilli(Long zoneOneMilli) {
        this.zoneOneMilli = zoneOneMilli;
    }

    public Long getZoneTwoMilli() {
        return zoneTwoMilli;
    }

    public void setZoneTwoMilli(Long zoneTwoMilli) {
        this.zoneTwoMilli = zoneTwoMilli;
    }

    public Long getZoneThreeMilli() {
        return zoneThreeMilli;
    }

    public void setZoneThreeMilli(Long zoneThreeMilli) {
        this.zoneThreeMilli = zoneThreeMilli;
    }

    public Long getZoneFourMilli() {
        return zoneFourMilli;
    }

    public void setZoneFourMilli(Long zoneFourMilli) {
        this.zoneFourMilli = zoneFourMilli;
    }

    public Long getZoneFiveMilli() {
        return zoneFiveMilli;
    }

    public void setZoneFiveMilli(Long zoneFiveMilli) {
        this.zoneFiveMilli = zoneFiveMilli;
    }
}
