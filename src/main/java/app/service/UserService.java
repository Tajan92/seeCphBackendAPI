package app.service;

import app.dao.UserDAO;
import app.dto.user.OrganizerDTORequest;
import app.dto.user.OrganizerDTOResponse;
import app.dto.user.UserDTORequest;
import app.dto.user.UserDTOResponse;
import app.entities.users.Organizer;
import app.entities.users.User;
import app.enums.Status;
import app.exceptions.BadRequestException;
import app.exceptions.ConflictException;
import app.mapper.UserConverter;
import app.utils.PasswordUtil;
import org.hibernate.service.spi.ServiceException;

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
        if (userDAO.checkIfEmailExist(input.email())){
            throw new ConflictException("Email is already in use");
        }
        String hashedPassword = PasswordUtil.hashPassword(input.password());
        User user = userDAO.create(userConverter.convertDTOToEntity(input, hashedPassword));
        return userConverter.convertEntityToDTO(user);
    }

    public OrganizerDTOResponse createPendingOrganizer(OrganizerDTORequest organizer) {
        if (userDAO.checkIfEmailExist(organizer.input().email())){
            throw new ConflictException("Email is already in use");
        }
        String hashedPassword = PasswordUtil.hashPassword(organizer.input().password());
        User user = userDAO.create(userConverter.convertDTOToOrganizer(organizer, hashedPassword));
        if (!(user instanceof Organizer)) {
            return null;
        }
        return userConverter.convertOrganizerToDTO((Organizer) user);
    }

    public UserDTOResponse createAdmin(UserDTORequest input) {
        if (userDAO.checkIfEmailExist(input.email())){
            throw new ConflictException("Email is already in use");
        }
        String hashedPassword = PasswordUtil.hashPassword(input.password());
        User user = userDAO.create(userConverter.convertDTOToAdmin(input, hashedPassword));
        return userConverter.convertEntityToDTO(user);
    }

    @Override
    public UserDTOResponse updateById(int id, UserDTORequest input) {
        User user = userDAO.readById(id);

        boolean emailChanged = !user.getEmail().equalsIgnoreCase(input.email());
        if (emailChanged && userDAO.checkIfEmailExist(input.email())) {
            throw new ConflictException("Email is already in use");
        }

        user.setName(input.name());
        user.setEmail(input.email());
        user.setPhone(input.phone());

        return userConverter.convertEntityToDTO(userDAO.update(user));
    }

    public OrganizerDTOResponse updateOrganizerStatus(int id, Status status) {
        User user = userDAO.readById(id);
        if (!(user instanceof Organizer organizer)) {
            throw new BadRequestException("User with id " + id + " is not an organizer");
        }
        organizer.setAccountStatus(status);
        Organizer updated = (Organizer) userDAO.update(organizer);
        return userConverter.convertOrganizerToDTO(updated);
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
