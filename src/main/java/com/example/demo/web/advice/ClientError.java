package com.example.demo.web.advice;

import com.example.demo.exception.OffendingValue;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Set;

/**
 * Represents a client error response with a key, message, offending values, and path.
 */

@Data
@Builder
public final class ClientError implements Serializable {

  private final String key;
  private final String message;
  private final Set<OffendingValue> offendingValues;
  private final String path;
}