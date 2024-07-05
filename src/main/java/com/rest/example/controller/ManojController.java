package com.rest.example.controller;

import com.rest.example.DTO.APIResonseDTO;
import com.rest.example.DTO.EmployeeDetailsDTO;
import com.rest.example.service.ImageUploadService;
import com.rest.example.service.ManojService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.owasp.encoder.Encode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import static com.rest.example.util.Constants.PROCESS_TIME;

@RestController
@RequestMapping(value = "/file/v1", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Slf4j
public class ManojController {

    //USER: ManojUser,Manoj@143, ManuGit@143

    private final ManojService manojService;

    private final ImageUploadService imageUploadService;

    @PostMapping(value = "/employee/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<String> uploadData(@RequestPart("employeeDetails") EmployeeDetailsDTO employeeDetailsDTO,
                                                     @RequestPart("employeeFile") List<MultipartFile> multipartFileList,
                                                     @RequestHeader Map<String, String> headers){
        log.info("Request Started");
        final Instant start = Instant.now();
        APIResonseDTO apiResonseDTO = manojService.uploadData(employeeDetailsDTO, multipartFileList);
        log.info("Request Ended");
        log.info(PROCESS_TIME, Encode.forJava(String.valueOf(Duration.between(start, Instant.now()))));
        return ResponseEntity.ok().body(apiResonseDTO.getResponseBody());
    }

    @PostMapping(value = "/image/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<String> uploadImage(@RequestPart("fileUpload") MultipartFile multipartFile,
                                              @RequestHeader Map<String, String> headers){
        log.info("Request Started");
        final Instant start = Instant.now();
        APIResonseDTO apiResonseDTO = imageUploadService.uploadImage(multipartFile);
        log.info("Request Ended");
        log.info(PROCESS_TIME, Encode.forJava(String.valueOf(Duration.between(start, Instant.now()))));
        return ResponseEntity.ok().body(apiResonseDTO.getResponseBody());
    }
}
