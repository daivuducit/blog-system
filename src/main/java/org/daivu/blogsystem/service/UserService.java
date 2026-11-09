package org.daivu.blogsystem.service;

import org.daivu.blogsystem.dto.request.UserCreateRequest;
import org.daivu.blogsystem.dto.request.UserUpdateRequest;
import org.daivu.blogsystem.dto.response.UserResponse;

import java.util.List;

public interface UserService {

    UserResponse createUser(UserCreateRequest request);

    UserResponse getUserById(Long id);

    UserResponse getCurrentUser();

    List<UserResponse> getAllUsers();

    UserResponse updateUser(Long id, UserUpdateRequest request);

    void deleteUser(Long id);
}
