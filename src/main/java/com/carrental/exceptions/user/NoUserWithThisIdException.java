package com.carrental.exceptions.user;

public class NoUserWithThisIdException extends UserOperationException {
    public NoUserWithThisIdException(String errorMessage) {
        super(errorMessage);
    }
}
