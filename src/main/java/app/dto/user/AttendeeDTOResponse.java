package app.dto.user;

import app.dto.event.EventDTOResponse;
import java.util.List;

public record AttendeeDTOResponse(
        UserDTOResponse userDTOResponse,
        List<EventDTOResponse> likedEvents,
        List<EventDTOResponse> favoriteEvents
) {
}
