package com.carrental.exceptions.rent;

public abstract class RentOperationException extends Exception {
    public RentOperationException(String errorMessage) {
        super(errorMessage);
    }
}
