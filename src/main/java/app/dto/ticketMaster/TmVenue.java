package app.dto.ticketMaster;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TmVenue(
        String id,
        String postalCode,
        TmCity city,
        TmAddress address,
        TmVenueLocation location
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record TmCity(String name) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record TmVenueLocation(String longitude, String latitude) {} // strings! not numbers

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record TmAddress(@JsonProperty("line1") String address) {}
}
