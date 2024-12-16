package com.example.demo.web.exception;

import com.example.demo.exception.ValidationException;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serializable;
import java.util.Set;

/**
 * Exception for not found.
 */

@Getter
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class NotFoundException extends ValidationException {

    public static final String NOT_FOUND_KEY = "resource.not.found";

    public NotFoundException(String message) {
        super(NOT_FOUND_KEY, null, message);
    }

    public NotFoundException(String message, Throwable cause) {
        super(NOT_FOUND_KEY, null, message, cause);
    }

    public NotFoundException(String resourceName, String fieldName, Serializable fieldValue) {
        super(NOT_FOUND_KEY,
                Set.of(build(resourceName, fieldName, fieldValue)),
                String.format("%s not found with %s : '%s'", resourceName, fieldName, fieldValue));
    }

    public NotFoundException(String key, String resourceName, String fieldName, Serializable fieldValue) {
        super(key,
                Set.of(build(resourceName, fieldName, fieldValue)),
                String.format("%s not found with %s : '%s'", resourceName, fieldName, fieldValue));
    }

    public NotFoundException(String resourceName, String fieldName, Serializable fieldValue, Throwable cause) {
        this(NOT_FOUND_KEY, resourceName, fieldName, fieldValue, "Resource not found", cause);
    }

    public NotFoundException(String key, String resourceName, String fieldName, Serializable fieldValue, String message,
                             Throwable cause) {
        super(key, Set.of(build(resourceName, fieldName, fieldValue)),
                message, cause);
    }
}
