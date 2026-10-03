package app.dto.user;

import app.enums.Status;
import app.enums.UserRole;

public record OrganizerDTOResponse(
        int id,
        String name,
        String email,
        String phone,
        UserRole userRole,
        Status status
) {
}
