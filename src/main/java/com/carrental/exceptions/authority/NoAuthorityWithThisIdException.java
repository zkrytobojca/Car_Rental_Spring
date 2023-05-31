package com.carrental.exceptions.authority;

public class NoAuthorityWithThisIdException extends AuthorityOperationException {
    public NoAuthorityWithThisIdException(String errorMessage) {
        super(errorMessage);
    }
}