package com.sparta.company.excpetion;

public class UserServiceNotAvailableException extends RuntimeException {
    public UserServiceNotAvailableException(String message) {
        super(message);
    }
}
