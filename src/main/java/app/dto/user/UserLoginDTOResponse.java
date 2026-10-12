package app.dto.user;

public record UserLoginDTOResponse(String token, UserDTOResponse user) {
}
