package app.service;

import app.dao.UserDAO;
import app.dto.event.EventDTOResponse;
import app.dto.user.*;
import app.entities.Event;
import app.entities.users.Organizer;
import app.entities.users.User;
import app.enums.FavoriteOrLikedEvent;
import app.enums.Status;
import app.exceptions.ConflictException;
import app.mapper.EventConverter;
import app.mapper.UserConverter;
import app.utils.PasswordUtil;

import java.util.ArrayList;
import java.util.List;

public class UserService {
    private final UserDAO userDAO;
    private final UserConverter userConverter;
    private final EventConverter eventConverter;

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
        this.userConverter = new UserConverter();
        this.eventConverter = new EventConverter();
    }

    public UserDTOResponse create(UserDTORequest input) {
        if (userDAO.checkIfEmailExist(input.email())) {
            throw new ConflictException("Email is already in use");
        }
        String hashedPassword = PasswordUtil.hashPassword(input.password());
        User user = userDAO.create(userConverter.convertDTOToEntity(input, hashedPassword));
        return userConverter.convertEntityToDTO(user);
    }

    public OrganizerDTOResponse createPendingOrganizer(OrganizerDTORequest organizer) {
        if (userDAO.checkIfEmailExist(organizer.input().email())) {
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
        if (userDAO.checkIfEmailExist(input.email())) {
            throw new ConflictException("Email is already in use");
        }
        String hashedPassword = PasswordUtil.hashPassword(input.password());
        User user = userDAO.create(userConverter.convertDTOToAdmin(input, hashedPassword));
        return userConverter.convertEntityToDTO(user);
    }

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
        Organizer organizer = userDAO.adminChangeStatusOfOrganizer(id, status);
        return userConverter.convertOrganizerToDTO(organizer);
    }

    public List<OrganizerDTOResponse> getAllOrganizersByStatus(Status status) {
        List<Organizer> pendingOrganizers = userDAO.adminReadOrganizersByStatus(status);
        return userConverter.convertEntitiesToDTOs(pendingOrganizers);
    }

    public UserDTOResponse getById(int id) {
        User user = userDAO.readById(id);
        return userConverter.convertEntityToDTO(user);
    }

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

    public void deleteById(int id) {
        User user = userDAO.readById(id);
        userDAO.delete(user);
    }

    public void addLikedEvent(int userId, int eventId) {
        userDAO.addFavoriteOrLikedEvent(userId, eventId, FavoriteOrLikedEvent.LIKED);
    }

    public List<EventDTOResponse> getFavoriteEvents(int userId) {
        List<Event> favoriteEvent = userDAO.readFavoriteOrLikedEvents(userId, FavoriteOrLikedEvent.FAVORITE);
        return eventConverter.convertEntityListToDTOList(favoriteEvent);
    }

    public List<EventDTOResponse> getLikedEvents(int userId) {
        List<Event> likedEvent = userDAO.readFavoriteOrLikedEvents(userId, FavoriteOrLikedEvent.LIKED);
        return eventConverter.convertEntityListToDTOList(likedEvent);
    }

    public void removeLikedEvent(int userId, int eventId) {
        userDAO.removeFavoriteOrLikedEvent(userId, eventId, FavoriteOrLikedEvent.LIKED);
    }

    public void addFavoriteEvent(int userId, int eventId) {
        userDAO.addFavoriteOrLikedEvent(userId, eventId, FavoriteOrLikedEvent.FAVORITE);
    }

    public void removeFavoriteEvent(int userId, int eventId) {
        userDAO.removeFavoriteOrLikedEvent(userId, eventId, FavoriteOrLikedEvent.FAVORITE);
    }

}
