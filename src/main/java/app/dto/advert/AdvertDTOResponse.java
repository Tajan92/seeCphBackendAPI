package app.dto.advert;

import app.dto.event.EventDTOResponse;
import app.dto.user.UserDTOResponse;
import app.entities.Advert;
import app.enums.AddPlacement;
import java.time.LocalDate;

public record AdvertDTOResponse(
        EventDTOResponse eventDTOResponse,
        UserDTOResponse userDTOResponse,
        Integer id,
        AddPlacement addPlacement,
        LocalDate startDate,
        LocalDate endDate
) {
    public AdvertDTOResponse(Advert advert, UserDTOResponse  user, EventDTOResponse event) {
        this(
                event,
                user,
                advert.getId(),
                advert.getAddPlacement(),
                advert.getStartDate(),
                advert.getEndDate());
    }
}
