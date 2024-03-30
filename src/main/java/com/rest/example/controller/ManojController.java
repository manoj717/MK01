package com.rest.example.controller;

import com.rest.example.DTO.APIResonseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@Controller
public class ManojController {

    private ResponseEntity<APIResonseDTO> getResponse(){
        return ResponseEntity.ok().body(new APIResonseDTO());
    }
}
