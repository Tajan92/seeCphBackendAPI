package app.mapper;

import app.dto.user.OrganizerDTORequest;
import app.dto.user.OrganizerDTOResponse;
import app.dto.user.UserDTORequest;
import app.dto.user.UserDTOResponse;
import app.entities.users.Admin;
import app.entities.users.Attendee;
import app.entities.users.Organizer;
import app.entities.users.User;
import app.enums.UserRole;

import java.util.ArrayList;
import java.util.List;

public class UserConverter {

    public User convertDTOToEntity(UserDTORequest attendeeDTO, String hashedPassword) {
        return Attendee.builder()
                .name(attendeeDTO.name())
                .email(attendeeDTO.email().toLowerCase().trim())
                .phone(attendeeDTO.phone())
                .hashedPassword(hashedPassword)
                .userRole(UserRole.ATTENDEE)
                .build();
    }

    public User convertDTOToOrganizer(OrganizerDTORequest organizerDTO, String hashedPassword) {
        return Organizer.builder()
                .name(organizerDTO.input().name())
                .email(organizerDTO.input().email().toLowerCase().trim())
                .phone(organizerDTO.input().phone())
                .organizerName(organizerDTO.organizerName())
                .hashedPassword(hashedPassword)
                .userRole(UserRole.ORGANIZER)
                .build();
    }

    public User convertDTOToAdmin(UserDTORequest adminDTORequest, String hashedPassword) {
        return Admin.builder()
                .name(adminDTORequest.name())
                .email(adminDTORequest.email().toLowerCase().trim())
                .phone(adminDTORequest.phone())
                .hashedPassword(hashedPassword)
                .userRole(UserRole.ADMIN)
                .build();
    }


//    public User convertDTOToUserUpdate(UserDTORequest input, User user, int id) {
//        User updatedUser = switch (user.getUserRole()){
//            case ADMIN -> Admin.builder().userId(id).build();
//            case ATTENDEE -> Attendee.builder().userId(id).build();
//            case ORGANIZER  -> Organizer.builder().userId(id).build();
//            case ANYONE -> null;
//        };
//        assert updatedUser != null;
//        updatedUser.setName(input.name());
//        updatedUser.setEmail(input.email().toLowerCase().trim());
//        updatedUser.setPhone(input.phone());
//
//        return updatedUser;
//    }

    public UserDTOResponse convertEntityToDTO(User user) {
        return new UserDTOResponse(user.getUserId(), user.getName(), user.getEmail(), user.getPhone(), user.getUserRole());
    }

    public OrganizerDTOResponse convertOrganizerToDTO(Organizer organizer) {
        return new OrganizerDTOResponse(organizer.getUserId(), organizer.getName(), organizer.getEmail(), organizer.getPhone(), organizer.getUserRole(), organizer.getAccountStatus());
    }

    public List<OrganizerDTOResponse> convertEntitiesToDTOs(List<Organizer> organizers) {
        List<OrganizerDTOResponse> dtos = new ArrayList<>();
        for (Organizer organizer : organizers) {
            dtos.add(convertOrganizerToDTO(organizer));
        }
        return dtos;
    }
}
