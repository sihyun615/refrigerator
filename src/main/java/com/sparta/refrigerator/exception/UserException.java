package com.sparta.refrigerator.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "UserException")
public class UserException extends RuntimeException {

    private ErrorCode errorCode;

    public UserException(ErrorCode errorCode){
        super(errorCode.getMsg());
        this.errorCode = errorCode;
        log.info("ExceptionMethod: {}", getExceptionMethod());
        log.info("ErrorCode: {}", errorCode.getMsg());
    }

    public String getExceptionMethod(){
        String className = Thread.currentThread().getStackTrace()[3].getClassName();
        String methodName = Thread.currentThread().getStackTrace()[3].getMethodName();
        return className + "." +methodName;
    }

}
