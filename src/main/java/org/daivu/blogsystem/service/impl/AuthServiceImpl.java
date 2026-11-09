package org.daivu.blogsystem.service.impl;

import org.daivu.blogsystem.dto.request.LoginRequest;
import org.daivu.blogsystem.dto.request.RegisterRequest;
import org.daivu.blogsystem.dto.response.AuthResponse;
import org.daivu.blogsystem.dto.response.UserResponse;
import org.daivu.blogsystem.entity.Role;
import org.daivu.blogsystem.entity.User;
import org.daivu.blogsystem.exception.AppException;
import org.daivu.blogsystem.exception.ErrorCode;
import org.daivu.blogsystem.repository.UserRepository;
import org.daivu.blogsystem.security.JwtService;
import org.daivu.blogsystem.security.TokenBlacklistService;
import org.daivu.blogsystem.service.AuthService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final TokenBlacklistService tokenBlacklistService;

    @Override
    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new AppException(ErrorCode.USERNAME_EXISTED);
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }

        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .fullName(request.fullName())
                .role(Role.USER) // đăng ký công khai luôn là USER
                .enabled(true)
                .build();

        return UserResponse.from(userRepository.save(user));
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        // Sai mật khẩu -> BadCredentialsException -> GlobalExceptionHandler trả 401
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));

        UserDetails principal = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByUsername(principal.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        String token = jwtService.generateToken(principal);
        return new AuthResponse(token, "Bearer", jwtService.getExpirationMs() / 1000,
                UserResponse.from(user));
    }

    @Override
    public void logout(String token) {
        // Đưa token vào blacklist đến khi hết hạn -> token này không dùng được nữa
        Claims claims = jwtService.decodeJwt(token);
        tokenBlacklistService.blacklist(claims.getId(), claims.getExpiration().toInstant());
    }
}
