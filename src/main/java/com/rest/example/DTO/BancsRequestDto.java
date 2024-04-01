package com.rest.example.DTO;

import lombok.Builder;

@Builder
public class BancsRequestDto {

    private int empId;
    private String name;
    private int mobNumber;

}
