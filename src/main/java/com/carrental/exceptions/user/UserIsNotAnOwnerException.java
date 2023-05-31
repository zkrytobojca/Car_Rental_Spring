package com.carrental.exceptions.user;

public class UserIsNotAnOwnerException extends UserOperationException {
    public UserIsNotAnOwnerException(String errorMessage) {
        super(errorMessage);
    }
}
