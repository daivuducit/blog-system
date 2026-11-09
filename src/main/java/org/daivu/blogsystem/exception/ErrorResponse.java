package org.daivu.blogsystem.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        Instant timestamp,
        int status,
        String code,
        String message,
        String path,
        Map<String, String> details
) {
    public static ErrorResponse of(ErrorCode errorCode, String path) {
        return new ErrorResponse(Instant.now(), errorCode.getStatus().value(),
                errorCode.name(), errorCode.getMessage(), path, null);
    }

    public static ErrorResponse of(ErrorCode errorCode, String path, Map<String, String> details) {
        return new ErrorResponse(Instant.now(), errorCode.getStatus().value(),
                errorCode.name(), errorCode.getMessage(), path, details);
    }
}
