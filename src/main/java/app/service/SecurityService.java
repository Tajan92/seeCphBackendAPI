package app.service;

import app.dao.UserDAO;
import app.dto.user.ChangePasswordDTORequest;
import app.dto.user.UserDTOResponse;
import app.dto.user.UserLoginDTORequest;
import app.entities.users.Organizer;
import app.entities.users.User;
import app.enums.Status;
import app.exceptions.BadRequestException;
import app.exceptions.SecurityValidationException;
import app.mapper.UserConverter;
import app.security.SecurityUtil;
import app.utils.PasswordUtil;
import io.javalin.http.HttpStatus;

public class SecurityService {
    private final UserDAO userDAO;
    private final UserConverter userConverter;

    public SecurityService(UserDAO userDAO) {
        this.userDAO = userDAO;
        this.userConverter = new UserConverter();
    }

    public UserDTOResponse login(UserLoginDTORequest userLoginDTORequest) {
        User user;
        try {
            user = userDAO.readUserByEmail(userLoginDTORequest.email().toLowerCase().trim());
        } catch (Exception e) {
            throw new SecurityValidationException(HttpStatus.UNAUTHORIZED.getCode(), "Invalid email or password");
        }
        SecurityUtil.verifyUserPassword(user, userLoginDTORequest);

        if (user instanceof Organizer organizer && organizer.getAccountStatus() != Status.ACTIVE) {
            throw new SecurityValidationException(HttpStatus.FORBIDDEN.getCode(), "Organizer account is not active");
        }
        return userConverter.convertEntityToDTO(user);
    }

    public void changePassword(int userId, ChangePasswordDTORequest dto) {
        if (!dto.newPassword().equals(dto.newPasswordCheck())) {
            throw new BadRequestException("New passwords do not match");
        }
        User user = userDAO.readById(userId);
        if (!PasswordUtil.verifyPassword(dto.password(), user)) {
            throw new SecurityValidationException(HttpStatus.UNAUTHORIZED.getCode(), "Current password is incorrect");
        }
        if (!dto.email().equals(user.getEmail())) {
            throw new SecurityValidationException(HttpStatus.UNAUTHORIZED.getCode(), "Email address do not match");
        }
        user.changePassword(dto.newPassword());
        userDAO.update(user);
    }
}
