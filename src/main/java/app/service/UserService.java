package app.service;

import app.dao.UserDAO;
import app.dto.user.UserDTORequest;
import app.dto.user.UserDTOResponse;

import java.util.List;

public class UserService implements IService<UserDTORequest, UserDTOResponse> {
    UserDAO userDAO;
    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public UserDTOResponse create(UserDTORequest input) {
        return null;
    }

    @Override
    public UserDTOResponse updateById(int id, UserDTORequest input) {
        return null;
    }

    @Override
    public UserDTOResponse getById(int id) {
        return null;
    }

    @Override
    public List<UserDTOResponse> getAll() {
        return List.of();
    }

    @Override
    public boolean deleteById(int id) {
        return false;
    }
}
