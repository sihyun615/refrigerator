package com.sparta.refrigerator.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    DUPLICATE_UESR(400, "중복된 사용자가 존재합니다."),
    INCORRECT_ADMIN(400, "관리자 암호가 일치하지 않습니다."),
    USER_NOT_FOUND(400, "해당 아이디의 유저를 찾지 못했습니다.");

    private int status;
    private String msg;
}
