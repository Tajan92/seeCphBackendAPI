package app.dto.user;

public record UserDTORequest (
        String name,
        String email,
        String phone,
        String password,
        String passwordCheck
) {

}
