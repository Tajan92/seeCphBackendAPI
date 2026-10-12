package app.utils;

import app.dto.user.UserLoginDTORequest;
import app.entities.users.User;
import app.exceptions.SecurityValidationException;

public class SecurityUtil {
    public static boolean verifyUserPassword(User user, UserLoginDTORequest userLoginDTORequest) {
        if (user != null && PasswordUtil.verifyPassword(userLoginDTORequest.password(), user)) {
            return true;
        } else  {
            throw new SecurityValidationException(401, "Invalid user or password");
        }
    }
}
