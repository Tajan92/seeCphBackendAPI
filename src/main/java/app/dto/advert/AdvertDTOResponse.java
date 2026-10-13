package app.dto.advert;

import app.dto.event.EventDTOResponse;
import app.dto.user.UserDTOResponse;
import app.enums.AddPlacement;
import java.time.LocalDate;

public record AdvertDTOResponse(
        EventDTOResponse eventDTOResponse,
        UserDTOResponse userDTOResponse,
        Integer id,
        AddPlacement addPlacement,
        LocalDate startDate,
        LocalDate endDate
) {}
