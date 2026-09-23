package app.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserRegisterDTO {
    @NotBlank(message = "Please enter name")
    private String name;
    @Email(message = "Email-format not right")
    @NotBlank(message = "Please enter email")
    private String email;
    @NotBlank(message = "Please enter phone number")
    private String phone;
    @NotBlank(message = "Please enter password (Length between 8-50)")
    @Size(min = 8, max = 50)
    private String password;
    @NotBlank(message = "Please enter password match (Length between 8-50)")
    @Size(min = 8, max = 50)
    private String passwordCheck;
}
