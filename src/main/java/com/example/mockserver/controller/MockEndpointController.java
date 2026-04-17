package com.example.mockserver.controller;

import com.example.mockserver.service.MockResponseService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class MockEndpointController {

    private final MockResponseService mockResponseService;

    public MockEndpointController(MockResponseService mockResponseService) {
        this.mockResponseService = mockResponseService;
    }

    @RequestMapping("/**")
    public ResponseEntity<String> mockAny(HttpServletRequest request) {
        return mockResponseService.buildMockResponse(request);
    }
}
