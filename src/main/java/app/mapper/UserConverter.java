package app.mapper;

import app.dto.user.UserDTORequest;
import app.dto.user.UserDTOResponse;
import app.entities.users.Admin;
import app.entities.users.Organizer;
import app.entities.users.User;
import app.enums.UserRole;

public class UserConverter implements IConverter<User, UserDTOResponse, UserDTORequest> {

    @Override
    public User convertDTOToEntity(UserDTORequest userDTO) {
//        User user = null;
//        if (userDTO.getUserRole() == UserRole.ADMIN) {
//            user = Admin.builder()
//                    .name(userDTO.getName())
//                    .email(userDTO.getEmail())
//                    .phone(userDTO.getPhone())
//                    .userRole(userDTO.getUserRole())
//                    .password(userDTO.getPassword())
//                    .build();
//        } else if (userDTO.getUserRole() == UserRole.ORGANIZER) {
//            user = Organizer.builder()
//                    .name(userDTO.getName())
//                    .email(userDTO.getEmail())
//                    .phone(userDTO.getPhone())
//                    .userRole(userDTO.getUserRole())
//                    .password(userDTO.getPassword())
//                    .build();
//        }
//        return user;
        return null;
    }

    @Override
    public UserDTOResponse convertEntityToDTO(User user) {
        return new UserDTOResponse(user.getUserId(), user.getName(), user.getEmail(), user.getPhone(), user.getUserRole());
    }
}
