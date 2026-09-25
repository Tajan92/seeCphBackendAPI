package app.dto.event;

import app.enums.EventCategory;
import java.time.LocalDate;
import java.time.LocalTime;

public record EventDTORequest(
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
        boolean useDefaultImage,
        EventCategory category
        ){
}
