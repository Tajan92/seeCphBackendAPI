package app.service;

import app.dao.UserDAO;
import app.dto.user.UserDTORequest;
import app.dto.user.UserDTOResponse;
import app.entities.users.Admin;
import app.entities.users.Attendee;
import app.entities.users.Organizer;
import app.entities.users.User;
import app.enums.UserRole;
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
        User user = Attendee.builder()
                .name(input.name())
                .email(input.email())
                .phone(input.phone())
                .password(input.password())
                .userRole(UserRole.ATTENDEE)
                .build();

        user = userDAO.create(user);

        return userConverter.convertEntityToDTO(user);
    }

    public UserDTOResponse createPendingOrganizer(UserDTORequest input) {
        User user = Organizer.builder()
                .name(input.name())
                .email(input.email())
                .phone(input.phone())
                .password(input.password())
                .userRole(UserRole.ORGANIZER)
                .build();

        user = userDAO.create(user);

        return userConverter.convertEntityToDTO(user);
    }

    public UserDTOResponse createAdmin(UserDTORequest input) {
        User user = Admin.builder()
                .name(input.name())
                .email(input.email())
                .phone(input.phone())
                .password(input.password())
                .userRole(UserRole.ADMIN)
                .build();

        user = userDAO.create(user);

        return userConverter.convertEntityToDTO(user);
    }

    @Override
    public UserDTOResponse updateById(int id, UserDTORequest input) {
        User user = userDAO.readById(id);
        user = switch (user.getUserRole()){
            case UserRole.ADMIN -> Admin.builder().userId(id).build();
            case UserRole.ATTENDEE -> Attendee.builder().userId(id).build();
            case UserRole.ORGANIZER  -> Organizer.builder().userId(id).build();
        };
        user.setName(input.name());
        user.setEmail(input.email());
        user.setPhone(input.phone());

        user = userDAO.update(user);
        return userConverter.convertEntityToDTO(user);
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
