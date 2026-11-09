package org.daivu.blogsystem.service;

import org.daivu.blogsystem.dto.request.LoginRequest;
import org.daivu.blogsystem.dto.request.RegisterRequest;
import org.daivu.blogsystem.dto.response.AuthResponse;
import org.daivu.blogsystem.dto.response.UserResponse;

public interface AuthService {

    UserResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    void logout(String token);
}
