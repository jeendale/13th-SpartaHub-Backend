package com.sparta.order.exception;

public class ServiceNotAvailableException extends RuntimeException {
  public ServiceNotAvailableException(String message) {
    super(message);
  }
}