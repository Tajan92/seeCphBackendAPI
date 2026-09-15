package app.dto.ticketMaster;

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
    public record TmGenre(String name) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record TmSubGenre(String name) {}
}
