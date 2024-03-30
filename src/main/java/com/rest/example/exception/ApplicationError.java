package com.rest.example.exception;

public interface ApplicationError {

    String getError();
    int getHttpCode();
    String getErrorCode();
}
