package org.daivu.blogsystem.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "Validation failed. Please check the request data."),
    UNAUTHENTICATED(HttpStatus.UNAUTHORIZED, "Authentication is required, or the access token is invalid or expired."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "Invalid username or password."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "You do not have permission to perform this action."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User not found."),
    USERNAME_EXISTED(HttpStatus.CONFLICT, "Username is already taken."),
    EMAIL_EXISTED(HttpStatus.CONFLICT, "Email is already registered."),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred. Please try again later.");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
