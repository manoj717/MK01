package com.rest.example.service;

import com.rest.example.DTO.APIResonseDTO;
import org.springframework.web.multipart.MultipartFile;

public interface ImageUploadService {

    APIResonseDTO uploadImage(final MultipartFile multipartFile);
}
