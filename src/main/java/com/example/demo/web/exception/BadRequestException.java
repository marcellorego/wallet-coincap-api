package com.example.demo.web.exception;

import com.example.demo.exception.OffendingValue;
import com.example.demo.exception.ValidationException;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Set;

/**
 * Exception for bad request.
 */

@Getter
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends ValidationException {

    public static final String BAD_REQUEST_KEY = "bad.request.parameters";

    public BadRequestException(final String key, final Set<OffendingValue> offendingValues, final String message) {
        super(key, offendingValues, message);
    }

    public BadRequestException(final Set<OffendingValue> offendingValues, final String message) {
        this(BAD_REQUEST_KEY, offendingValues, message);
    }
    public BadRequestException(final Set<OffendingValue> offendingValues, final String message, final Throwable cause) {
        super(BAD_REQUEST_KEY, offendingValues, message, cause);
    }

    public BadRequestException(final String key, final Set<OffendingValue> offendingValues, final String message, final Throwable cause) {
        super(key, offendingValues, message, cause);
    }
}