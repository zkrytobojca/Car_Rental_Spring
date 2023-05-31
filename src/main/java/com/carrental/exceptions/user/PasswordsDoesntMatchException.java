package com.carrental.exceptions.user;

public class PasswordsDoesntMatchException extends UserOperationException {
    public PasswordsDoesntMatchException(String errorMessage) {
        super(errorMessage);
    }
}
