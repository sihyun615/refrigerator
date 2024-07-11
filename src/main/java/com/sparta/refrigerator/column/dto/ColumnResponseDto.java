package com.sparta.refrigerator.column.dto;

import com.sparta.refrigerator.column.entity.StatusEnum;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ColumnResponseDto {
    private StatusEnum status;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    @Builder
    public ColumnResponseDto(StatusEnum status, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.status = status;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }
}
