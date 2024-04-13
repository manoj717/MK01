package com.rest.example.DTO;

import lombok.Getter;

import java.util.List;

@Getter
public class EmployeeDetailsDTO {

    private int empId;
    private String name;
    private String mobNumber;

    private double salary;

    private String address;

    private List<EmployeeFileDTO> employeeFiles;
}
