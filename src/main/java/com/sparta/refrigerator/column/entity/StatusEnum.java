package com.sparta.refrigerator.column.entity;

public enum StatusEnum {
    STORED("Stored"),
    IN_USE("In Use"),
    USED("Used");

    private final String status;

    StatusEnum(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
