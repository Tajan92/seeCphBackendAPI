package app.service;

import app.dao.UserDAO;
import app.dto.user.UserDTOResponse;
import app.dto.user.UserLoginDTORequest;
import app.entities.users.Organizer;
import app.entities.users.User;
import app.enums.Status;
import app.exceptions.SecurityValidationException;
import app.mapper.UserConverter;
import app.utils.SecurityUtil;

public class SecurityService {
    private final UserDAO userDAO;
    private final UserConverter userConverter;

    public SecurityService(UserDAO userDAO) {
        this.userDAO = userDAO;
        this.userConverter = new UserConverter();
    }

    public UserDTOResponse login(UserLoginDTORequest userLoginDTORequest) {
        User user = userDAO.readUserByEmail(userLoginDTORequest.email().toLowerCase().trim());
        SecurityUtil.verifyUserPassword(user, userLoginDTORequest);

        if (user instanceof Organizer organizer && organizer.getAccountStatus() != Status.ACTIVE) {
            throw new SecurityValidationException(403, "Organizer account is not active");
        }

        return userConverter.convertEntityToDTO(user);
    }
}
