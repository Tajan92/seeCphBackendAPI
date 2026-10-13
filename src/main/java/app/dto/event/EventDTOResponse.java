package app.dto.event;

import java.time.LocalDate;
import java.time.LocalTime;

public record EventDTOResponse(
        Integer id,
        String title,
        String description,
        Double price,
        boolean free,
        String city,
        String postalCode,
        String address,
        LocalTime startTime,
        LocalDate startDate,
        String url,
        String primaryImageUrl,
        String category
){}
