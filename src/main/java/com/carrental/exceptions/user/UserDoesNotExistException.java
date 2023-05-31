package com.carrental.exceptions.user;

public class UserDoesNotExistException extends UserOperationException {
    public UserDoesNotExistException(String errorMessage) {
        super(errorMessage);
    }
}
