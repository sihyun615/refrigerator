package com.sparta.refrigerator.column.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ColumnRequestDto {
    @NotBlank(message = "추가할 컬럼의 이름을 작성해주세요.")
    private String columnName;
}
