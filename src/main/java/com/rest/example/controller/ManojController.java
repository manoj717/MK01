package com.rest.example.controller;

import com.rest.example.DTO.APIResonseDTO;
import com.rest.example.DTO.EmployeeDetailsDTO;
import com.rest.example.service.ManojService;
import com.rest.example.service.ManojServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/file/v1")
@RequiredArgsConstructor
@Slf4j
public class ManojController {

    //USER: ManojUser,Manu@143, ManuGit@143

    @Autowired
    private ManojServiceImpl manojService;

    @PostMapping(value = "/employee/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<APIResonseDTO> uploadData(@RequestPart("employeeDetails") EmployeeDetailsDTO employeeDetailsDTO,
                                                     @RequestPart("employeeFile") List<MultipartFile> multipartFileList,
                                                     @RequestHeader Map<String, String> headers){
        log.info("Request Started");
        APIResonseDTO apiResonseDTO = manojService.uploadData(employeeDetailsDTO, multipartFileList);
        log.info("Request Ended");
        return ResponseEntity.ok().body(apiResonseDTO);
    }
}
