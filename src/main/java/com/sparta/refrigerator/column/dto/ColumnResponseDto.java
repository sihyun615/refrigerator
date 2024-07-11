package com.sparta.refrigerator.column.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ColumnResponseDto {

    private String columnName;
    private Long columnIndex;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    @Builder
    public ColumnResponseDto(String columnName, Long columnIndex, LocalDateTime createdAt,
        LocalDateTime modifiedAt) {
        this.columnName = columnName;
        this.columnIndex = columnIndex;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }
}
