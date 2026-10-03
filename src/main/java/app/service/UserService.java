package app.service;

import app.dao.UserDAO;
import app.dto.user.OrganizerDTOResponse;
import app.dto.user.UserDTORequest;
import app.dto.user.UserDTOResponse;
import app.entities.users.Organizer;
import app.entities.users.User;
import app.mapper.UserConverter;

import java.util.ArrayList;
import java.util.List;

public class UserService implements IService<UserDTORequest, UserDTOResponse> {
    UserDAO userDAO;
    UserConverter userConverter;

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
        this.userConverter = new UserConverter();
    }

    @Override
    public UserDTOResponse create(UserDTORequest input) {
        User user = userDAO.create(userConverter.convertDTOToEntity(input));
        return userConverter.convertEntityToDTO(user);
    }

    public OrganizerDTOResponse createPendingOrganizer(UserDTORequest input) {
        User user = userDAO.create(userConverter.convertDTOToOrganizer(input));
        if (!(user instanceof Organizer)) {
            return null;
        }
        return userConverter.convertOrganizerToDTO((Organizer) user);
    }

    public UserDTOResponse createAdmin(UserDTORequest input) {
        User user = userDAO.create(userConverter.convertDTOToAdmin(input));
        return userConverter.convertEntityToDTO(user);
    }

    @Override
    public UserDTOResponse updateById(int id, UserDTORequest input) {
        User user = userDAO.readById(id);
        User updatedUser = userDAO.update(userConverter.convertDTOToUserUpdate(input, user, id));
        return userConverter.convertEntityToDTO(updatedUser);
    }

    @Override
    public UserDTOResponse getById(int id) {
        User user = userDAO.readById(id);
        return userConverter.convertEntityToDTO(user);
    }

    @Override
    public List<UserDTOResponse> getAll() {
        List<User> users = userDAO.readAll();
        List<UserDTOResponse> userDTOs = new ArrayList<>();
        if (users != null && !users.isEmpty()) {
            for (User user : users) {
                userDTOs.add(userConverter.convertEntityToDTO(user));
            }
        }
        return userDTOs;
    }

    @Override
    public boolean deleteById(int id) {
        User user = userDAO.readById(id);
        return userDAO.delete(user);
    }
}
