package com.carrental.models.mt;

import com.carrental.models.enums.OperationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class OperationDetails {
    private OperationStatus operationStatus;
    private String message;

    public void setOperationStatus(OperationStatus operationStatus) {
        this.operationStatus = operationStatus;
        this.message = operationStatus.getMessage();
    }
}
