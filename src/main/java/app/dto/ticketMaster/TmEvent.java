package app.dto.ticketMaster;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TmEvent(
        String id,
        String name,
        String url,
        String info,               // description
        TmDates dates,
        List<TmClassification> classifications,
        List<TmPriceRange> priceRanges,   // often null/absent — must be nullable
        @JsonProperty("_embedded") TmEventEmbedded embedded,
        List<TmImage> images
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record TmEventEmbedded(
            List<TmVenue> venues,
            List<TmAttraction> attractions
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record TmImage(
            String ratio,
            String url,
            int width,
            int height
    ) {
    }
}

