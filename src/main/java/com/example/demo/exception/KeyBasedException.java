package com.example.demo.exception;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

/**
 * Bae Exception class with a key
 */

@Getter
@JsonIgnoreProperties({"cause", "stackTrace", "suppressed"})
public class KeyBasedException extends RuntimeException {

    private final String key;

    public KeyBasedException(String key) {
        this.key = key;
    }

    public KeyBasedException(String key, String message) {
        super(message);
        this.key = key;
    }

    public KeyBasedException(String key, String message, Throwable cause) {
        super(message, cause);
        this.key = key;
    }

    public KeyBasedException(String key, Throwable cause) {
        super(cause);
        this.key = key;
    }

    public KeyBasedException(String key, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.key = key;
    }
}
