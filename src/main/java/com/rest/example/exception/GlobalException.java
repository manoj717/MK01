package com.rest.example.exception;

public class GlobalException extends RuntimeException{

    private ApplicationError applicationError;

    private GlobalException(ApplicationError applicationError){
        this.applicationError=applicationError;
    }
}
