package com.carrental.models.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OperationStatus {
    ERROR(false, "Some error occur"),
    SUCCESS(true, "Success"),
    CANCELLED(true, "Reservation cancelled");

    private final boolean valid;
    private final String message;
}
