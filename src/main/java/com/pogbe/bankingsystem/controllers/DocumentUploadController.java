package com.pogbe.bankingsystem.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/document")
public class DocumentUploadController {

    @PostMapping("")
    public String uploadDocument() {
        return "";
    }
}
