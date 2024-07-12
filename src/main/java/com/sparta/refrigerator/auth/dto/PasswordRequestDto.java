package com.sparta.refrigerator.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PasswordRequestDto {

    /**
     * 비밀번호
     */
    @NotBlank(message = "패스워드를 입력해주세요.")
    private String password;
}