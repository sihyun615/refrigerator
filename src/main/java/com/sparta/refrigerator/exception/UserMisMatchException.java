package com.sparta.refrigerator.exception;

public class UserMisMatchException extends RuntimeException {
    public UserMisMatchException(String message) {
        super(message);
    }
}