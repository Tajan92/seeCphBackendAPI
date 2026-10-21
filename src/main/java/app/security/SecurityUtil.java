package app.security;

import app.dto.user.UserLoginDTORequest;
import app.entities.users.User;
import app.exceptions.SecurityValidationException;
import app.utils.PasswordUtil;

public class SecurityUtil {
    public static void verifyUserPassword(User user, UserLoginDTORequest userLoginDTORequest) {
        if (user == null || !PasswordUtil.verifyPassword(userLoginDTORequest.password(), user)) {
            throw new SecurityValidationException(401, "Invalid user or password");
        }
    }
}
