package com.sparta.refrigerator.board.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class BoardRequestDTO {

    @NotBlank(message = "추가할 보드의 이름을 입력해주세요.")
    private String boardName;
    @NotBlank(message = "추가할 보드의 내용을 입력해주세요.")
    private String boardInfo;

}
