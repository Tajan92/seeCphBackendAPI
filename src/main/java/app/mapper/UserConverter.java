package app.mapper;

import app.dto.user.UserDTORequest;
import app.dto.user.UserDTOResponse;
import app.entities.users.Admin;
import app.entities.users.Attendee;
import app.entities.users.Organizer;
import app.entities.users.User;
import app.enums.UserRole;

public class UserConverter implements IConverter<User, UserDTOResponse, UserDTORequest> {

    @Override
    public User convertDTOToEntity(UserDTORequest attendeeDTO) {
        return Attendee.builder()
                .name(attendeeDTO.name())
                .email(attendeeDTO.email())
                .phone(attendeeDTO.phone())
                .password(attendeeDTO.password())
                .userRole(UserRole.ATTENDEE)
                .build();
    }

    public User convertDTOToOrganizer(UserDTORequest organizerDTO) {
        return Organizer.builder()
                .name(organizerDTO.name())
                .email(organizerDTO.email())
                .phone(organizerDTO.phone())
                .password(organizerDTO.password())
                .userRole(UserRole.ORGANIZER)
                .build();
    }

    public User convertDTOToAdmin(UserDTORequest adminDTORequest) {
        return Admin.builder()
                .name(adminDTORequest.name())
                .email(adminDTORequest.email())
                .phone(adminDTORequest.phone())
                .password(adminDTORequest.password())
                .userRole(UserRole.ADMIN)
                .build();
    }

    public User convertDTOToUserUpdate(UserDTORequest input, User user, int id) {
        User updatedUser = switch (user.getUserRole()){
            case UserRole.ADMIN -> Admin.builder().userId(id).build();
            case UserRole.ATTENDEE -> Attendee.builder().userId(id).build();
            case UserRole.ORGANIZER  -> Organizer.builder().userId(id).build();
        };
        user.setName(input.name());
        user.setEmail(input.email());
        user.setPhone(input.phone());

        return updatedUser;
    }

    @Override
    public UserDTOResponse convertEntityToDTO(User user) {
        return new UserDTOResponse(user.getUserId(), user.getName(), user.getEmail(), user.getPhone(), user.getUserRole());
    }
}
