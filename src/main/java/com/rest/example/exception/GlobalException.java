package com.rest.example.exception;

public class GlobalException extends RuntimeException{

    private ApplicationError applicationError;

    public GlobalException(ApplicationError applicationError){
        this.applicationError=applicationError;
    }
}
