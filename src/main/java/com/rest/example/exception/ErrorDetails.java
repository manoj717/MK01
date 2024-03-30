package com.rest.example.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorDetails implements ApplicationError{

    SERVICE_UNAVAILABLE("Service Unavailable, Please retry !!", HttpStatus.SERVICE_UNAVAILABLE.value(), "MS-01");

    private String error;
    private int httpCode;
    private String errorCode;
}
