package com.carrental.exceptions.user;

public abstract class UserOperationException extends Exception {

    public UserOperationException(String errorMessage) {
        super(errorMessage);
    }
}