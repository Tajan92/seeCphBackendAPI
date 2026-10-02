package app.dto.event;

import app.entities.Event;
import java.time.LocalDate;
import java.time.LocalTime;

public record EventDTOResponse(
        Integer id,
        String title,
        String description,
        double price,
        boolean free,
        String city,
        String postalCode,
        String address,
        LocalTime startTime,
        LocalDate startDate,
        String url,
        String primaryImageUrl,
        String category
){
    public EventDTOResponse(Event event) {
        this(
                event.getEventId(),
                event.getTitle(),
                event.getDescription(),
                event.getPrice(),
                event.isFree(),
                event.getLocation() != null ? event.getLocation().getCity() : null,
                event.getLocation() !=null ? event.getLocation().getPostalCode() : null,
                event.getLocation() != null ? event.getLocation().getAddress() : null,
                event.getStartTime(),
                event.getStartDate(),
                event.getUrl(),
                event.getImageUrl(),
                event.getCategory().name());
    }
}
