package com.rest.example.DTO;

import lombok.Builder;

@Builder
public class BancsRequestDto {

    @Override
    public String toString() {
        return "BancsRequestDto{" +
                "empId=" + empId +
                ", name='" + name + '\'' +
                ", mobNumber='" + mobNumber + '\'' +
                '}';
    }

    private int empId;
    private String name;
    private String mobNumber;

}
