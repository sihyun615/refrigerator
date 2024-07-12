package com.sparta.refrigerator.board.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class InvitationRequestDTO {

    @NotBlank(message = "초대할 사용자 이름을 입력해주세요.")
    private String userName;
}
