package org.daivu.blogsystem.service.impl;

import org.daivu.blogsystem.dto.request.UserCreateRequest;
import org.daivu.blogsystem.dto.request.UserUpdateRequest;
import org.daivu.blogsystem.dto.response.UserResponse;
import org.daivu.blogsystem.entity.Role;
import org.daivu.blogsystem.entity.User;
import org.daivu.blogsystem.exception.AppException;
import org.daivu.blogsystem.exception.ErrorCode;
import org.daivu.blogsystem.repository.UserRepository;
import org.daivu.blogsystem.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse createUser(UserCreateRequest request) {
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
                .role(request.role() != null ? request.role() : Role.USER)
                .enabled(true)
                .build();

        return UserResponse.from(userRepository.save(user));
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User target = findById(id);
        assertAdminOrSelf(target);
        return UserResponse.from(target);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser() {
        return UserResponse.from(loadCurrentUser());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream().map(UserResponse::from).toList();
    }

    @Override
    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        User target = findById(id);
        User current = loadCurrentUser();
        boolean isAdmin = current.getRole() == Role.ADMIN;

        if (!isAdmin && !current.getId().equals(target.getId())) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }

        if (request.email() != null && !request.email().equals(target.getEmail())) {
            if (userRepository.existsByEmail(request.email())) {
                throw new AppException(ErrorCode.EMAIL_EXISTED);
            }
            target.setEmail(request.email());
        }
        if (request.fullName() != null) {
            target.setFullName(request.fullName());
        }
        if (request.password() != null) {
            target.setPassword(passwordEncoder.encode(request.password()));
        }

        // role và enabled chỉ ADMIN mới được đổi, user thường gửi lên sẽ bị từ chối
        if (request.role() != null || request.enabled() != null) {
            if (!isAdmin) {
                throw new AppException(ErrorCode.ACCESS_DENIED);
            }
            if (request.role() != null) {
                target.setRole(request.role());
            }
            if (request.enabled() != null) {
                target.setEnabled(request.enabled());
            }
        }

        return UserResponse.from(userRepository.save(target));
    }

    @Override
    public void deleteUser(Long id) {
        User target = findById(id);
        userRepository.delete(target);
    }

    // ---------- helpers ----------

    private User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    private User loadCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));
    }

    private void assertAdminOrSelf(User target) {
        User current = loadCurrentUser();
        if (current.getRole() != Role.ADMIN && !current.getId().equals(target.getId())) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }
    }
}
