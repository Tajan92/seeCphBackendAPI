package app.service;

import app.dao.UserDAO;
import app.dto.user.UserDTOResponse;
import app.dto.user.UserLoginDTORequest;
import app.entities.users.User;
import app.mapper.UserConverter;
import app.utils.SecurityUtil;

public class SecurityService {
    private UserDAO userDAO;
    private UserConverter userConverter;

    public SecurityService(UserDAO userDAO) {
        this.userDAO = userDAO;
        this.userConverter = new UserConverter();
    }

    public UserDTOResponse login(UserLoginDTORequest userLoginDTORequest) {
        User user = userDAO.readUserByEmail(userLoginDTORequest.email());
        if (SecurityUtil.verifyUser(user, userLoginDTORequest)) {
            return userConverter.convertEntityToDTO(user);
        } else  {
            return null;
        }
    }
}
