package com.sparta.refrigerator.column.entity;

public enum StatusEnum {
    STORED(1L),
    IN_USE(2L),
    USED(3L);

    private final Long status;

    StatusEnum(Long status) {
        this.status = status;
    }

    public Long getStatus() {
        return status;
    }
}
