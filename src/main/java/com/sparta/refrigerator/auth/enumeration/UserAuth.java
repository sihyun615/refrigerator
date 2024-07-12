package com.sparta.refrigerator.auth.enumeration;

import lombok.Getter;

@Getter
public enum UserAuth {
    USER("USER"),
    MANAGER("MANAGER"),
    ACTIVE("ACTIVE"),
    WITHDRAW("WITHDRAW");


    private final String auth;

    UserAuth(String auth) {
        this.auth = auth;
    }

    @Override
    public String toString() {
        return this.auth;
    }

}