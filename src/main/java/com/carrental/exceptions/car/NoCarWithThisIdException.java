package com.carrental.exceptions.car;

public class NoCarWithThisIdException extends CarOperationException {
    public NoCarWithThisIdException(String errorMessage) {
        super(errorMessage);
    }
}
