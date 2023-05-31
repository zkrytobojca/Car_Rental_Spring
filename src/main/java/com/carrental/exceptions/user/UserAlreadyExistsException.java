package com.carrental.exceptions.user;

public class UserAlreadyExistsException extends UserOperationException {
    public UserAlreadyExistsException(String errorMessage) {
        super(errorMessage);
    }
}
