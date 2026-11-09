package org.daivu.blogsystem.dto.request;

import org.daivu.blogsystem.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

/** Các field để null nghĩa là không thay đổi. Chỉ ADMIN mới được đổi role / enabled. */
public record UserUpdateRequest(
        @Email(message = "Email must be a valid email address")
        String email,

        @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
        String password,

        @Size(max = 100, message = "Full name must not exceed 100 characters")
        String fullName,

        Role role,

        Boolean enabled
) {
}
