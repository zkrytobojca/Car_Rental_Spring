package com.carrental.exceptions.car;

public abstract class CarOperationException extends Exception {
    public CarOperationException(String errorMessage) {
        super(errorMessage);
    }
}
