package app.dto.user;

public record ChangePasswordDTORequest (String email,  String password, String newPassword, String newPasswordCheck){
}
