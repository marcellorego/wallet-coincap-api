package com.example.demo.service.parser;

import com.example.demo.exception.OffendingValue;
import com.example.demo.exception.ValidationException;

import java.util.Set;

/**
 * Exception thrown when a parsing error occurs.
 */

public class ParseException extends ValidationException {

  public ParseException(final OffendingValue offendingValue, final String message) {
    super(Set.of(offendingValue), message);
  }
}
