package app.dto.ticketMaster;

import app.enums.EventCategory;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TmClassification(
        boolean primary,
        TmSegment segment,
        TmGenre genre,
        TmSubGenre subGenre
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record TmSegment(String name) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record TmGenre(EventCategory name) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record TmSubGenre(EventCategory name) {}
}
