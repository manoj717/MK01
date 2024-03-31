package com.rest.example.DTO;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class APIResonseDTO {

    private int statusCode;
    private String responseBody;
}
