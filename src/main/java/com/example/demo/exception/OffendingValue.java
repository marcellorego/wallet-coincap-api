package com.example.demo.exception;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * Offending Value class
 * This class contains the offending value
 */

@Builder
@Data
@EqualsAndHashCode(of = {"field"})
public class OffendingValue implements Serializable {
    private String field;
    private String message;
    private String invalidValue;
}
