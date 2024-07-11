package com.sparta.refrigerator.auth.enumeration;

public enum UserAuth {
    USER("USER"),
    ADMIN("ADMIN");

    private final String status;

    UserAuth(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return this.status;
    }

}