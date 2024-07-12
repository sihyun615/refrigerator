package com.sparta.refrigerator.card.util;

public class SuccessResponse<T> {
    private T data;
    private String message;
    private int status;

    // 기본 생성자
    public SuccessResponse() {}

    // 매개변수가 있는 생성자
    public SuccessResponse(T data) {
        this.data = data;
        this.message = "Success";
        this.status = 200;
    }

    // 매개변수가 있는 생성자
    public SuccessResponse(T data, String message, int status) {
        this.data = data;
        this.message = message;
        this.status = status;
    }

    // Getters and Setters
    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
