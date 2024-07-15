package com.sparta.refrigerator.card.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class CardRequestDto {

    @NotBlank(message = "추가할 제목을 입력해주세요")
    private String title;

    @NotBlank(message = "추가할 내용을 입력해주세요.")
    private String content;

    private String collaborator;
    private String deadline;
}
