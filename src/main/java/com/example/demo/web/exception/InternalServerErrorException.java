package com.example.demo.web.exception;

import com.example.demo.exception.KeyBasedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception for internal server error.
 */

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class InternalServerErrorException extends KeyBasedException {

    public static final String SERVER_ERROR_KEY = "internal.server.error";

    public InternalServerErrorException(String message) {
        super(SERVER_ERROR_KEY, message);
    }

    public InternalServerErrorException(String key, String message) {
        super(key, message);
    }

    public InternalServerErrorException(String message, Throwable cause) {
        super(SERVER_ERROR_KEY, message, cause);
    }

    public InternalServerErrorException(String key, String message, Throwable cause) {
        super(key, message, cause);
    }
}