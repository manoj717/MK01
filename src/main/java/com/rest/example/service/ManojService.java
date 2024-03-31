package com.rest.example.service;

import com.rest.example.DTO.APIResonseDTO;
import com.rest.example.DTO.EmployeeDetailsDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ManojService {

    APIResonseDTO uploadData(EmployeeDetailsDTO employeeDetailsDTO, List<MultipartFile> multipartFileList);
}
