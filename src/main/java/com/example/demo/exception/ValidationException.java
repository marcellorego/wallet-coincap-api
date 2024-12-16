package com.example.demo.exception;

import lombok.Getter;

import java.io.Serializable;
import java.util.Set;

/**
 * Validation Exception class
 * This exception is thrown when a validation error occurs
 * Contains the offending value
 */

@Getter
public class ValidationException extends KeyBasedException {

    public static final String VALIDATION_KEY = "resource.validation";

    private final transient Set<OffendingValue> offendingValues;

    public ValidationException(final String key, final Set<OffendingValue> offendingValues,
                               final String message) {
        super(key, message);
        this.offendingValues = offendingValues;
    }

    public ValidationException(final Set<OffendingValue> offendingValues, final String message) {
        this(VALIDATION_KEY, offendingValues, message);
    }

    public ValidationException(final Set<OffendingValue> offendingValues, String message, Throwable cause) {
        super(VALIDATION_KEY, message, cause);
        this.offendingValues = offendingValues;
    }

    public ValidationException(String key, final Set<OffendingValue> offendingValues, String message, Throwable cause) {
        super(key, message, cause);
        this.offendingValues = offendingValues;
    }

    public static OffendingValue build(String resourceName, String fieldName, Serializable fieldValue) {
        return OffendingValue.builder()
                .message(resourceName)
                .field(fieldName)
                .invalidValue(String.valueOf(fieldValue))
                .build();
    }
}
