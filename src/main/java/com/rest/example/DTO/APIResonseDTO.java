package com.rest.example.DTO;

import lombok.Builder;

@Builder
public class APIResonseDTO {

    private int statusCode;
    private String responseBody;
}
