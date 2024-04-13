package com.rest.example.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class ManojControllerAdvice {


    @ExceptionHandler(InvalidBearerTokenException.class)
    protected ResponseEntity<String> invalidToken(InvalidBearerTokenException ex){
        log.info("Error : ",ex.fillInStackTrace());
        return ResponseEntity.status(401).body("Invalid Token");
    }

    @ExceptionHandler(InsufficientAuthenticationException.class)
    protected ResponseEntity<String> handleAuthException(InsufficientAuthenticationException ex){
        log.info("Error : ",ex.fillInStackTrace());
        return ResponseEntity.status(401).body("Token Is Empty");
    }
}
