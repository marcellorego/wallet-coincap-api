package com.example.demo.service.client;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;

/**
 * CoinCap exception.
 */

@Getter
public class CoinCapException extends RuntimeException {

  private final HttpStatusCode statusCode;

  public CoinCapException(String message, HttpStatusCode statusCode, Throwable cause) {
    super(message, cause);
    this.statusCode = statusCode;
  }
}
