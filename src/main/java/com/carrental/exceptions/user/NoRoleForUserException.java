package com.carrental.exceptions.user;

public class NoRoleForUserException extends UserOperationException {
    public NoRoleForUserException(String errorMessage) {
        super(errorMessage);
    }
}
