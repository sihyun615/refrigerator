package com.sparta.refrigerator.column.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ColumnResponseDto {

    private String columnName;
    private Long columnIndex;

    @Builder
    public ColumnResponseDto(String columnName, Long columnIndex) {
        this.columnName = columnName;
        this.columnIndex = columnIndex;
    }
}
