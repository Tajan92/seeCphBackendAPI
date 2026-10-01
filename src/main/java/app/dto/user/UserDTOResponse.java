package app.dto.user;

import app.enums.UserRole;

public record UserDTOResponse(int id, String firstName, String email, String phone, UserRole userRole) {
}
