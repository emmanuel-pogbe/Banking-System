package com.pogbe.bankingsystem.controllers;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/test")
public class TestController {

    @Operation(summary = "Ping the server")
    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("pong");
    }

    @PostMapping("/image")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        System.out.println("\n\n\n\n");
        System.out.println("Name of parameter: "+file.getName());
        System.out.println("Original name of file: "+file.getOriginalFilename());
        System.out.println("Size of file: "+file.getSize());
        System.out.println("Content type of file: "+file.getContentType());

        System.out.println("\n\n\n\n");


        return ResponseEntity.ok("Image uploaded successfully");
    }

}
