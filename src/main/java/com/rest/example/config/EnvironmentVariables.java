package com.rest.example.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class EnvironmentVariables {

    @Value("${aws.auth}")
    private boolean awsAuth;


}
