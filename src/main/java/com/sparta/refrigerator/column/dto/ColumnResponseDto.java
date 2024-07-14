package com.sparta.refrigerator.column.dto;

import com.sparta.refrigerator.column.entity.Columns;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ColumnResponseDto {

    private Long columnId;
    private String columnName;
    private Long columnIndex;

    public ColumnResponseDto(Columns columns) {
        this.columnId=columns.getId();
        this.columnName = columns.getColumnName();
        this.columnIndex = columns.getColumnIndex();
    }
}