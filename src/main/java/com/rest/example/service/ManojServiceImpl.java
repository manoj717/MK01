package com.rest.example.service;

import com.rest.example.DTO.APIResonseDTO;
import com.rest.example.DTO.EmployeeDetailsDTO;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ManojServiceImpl implements ManojService {
    @Override
    public APIResonseDTO uploadData(EmployeeDetailsDTO employeeDetailsDTO, List<MultipartFile> multipartFileList) {

        return APIResonseDTO.builder().statusCode(200).responseBody("Success").build();
    }
}
