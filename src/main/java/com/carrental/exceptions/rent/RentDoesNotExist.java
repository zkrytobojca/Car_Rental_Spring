package com.carrental.exceptions.rent;

public class RentDoesNotExist extends RentOperationException {
    public RentDoesNotExist(String errorMessage) {
        super(errorMessage);
    }
}
