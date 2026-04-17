package com.example.mockserver.service;

import com.example.mockserver.model.MockResponseMetadata;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
public class MockResponseService {

    private static final String API_PREFIX = "/api/";
    private static final String DEFAULT_NOT_FOUND_TEMPLATE = "{\"error\":\"mock not found\",\"path\":\"%s\"}";
    private final ObjectMapper objectMapper;

    public MockResponseService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public ResponseEntity<String> buildMockResponse(HttpServletRequest request) {
        String mockKey = normalizePath(request.getRequestURI());

        String bodyPath = "mocks/" + mockKey + ".json";
        String metadataPath = "mocks/" + mockKey + ".meta.json";

        String responseBody = readClasspathFileOrNull(bodyPath);
        if (responseBody == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(DEFAULT_NOT_FOUND_TEMPLATE.formatted(mockKey));
        }

        MockResponseMetadata metadata = readMetadataOrDefault(metadataPath);

        if (metadata.getDelayMs() != null && metadata.getDelayMs() > 0) {
            sleep(metadata.getDelayMs());
        }

        HttpHeaders headers = new HttpHeaders();
        applyContentType(headers, metadata.getContentType());
        applyHeaders(headers, metadata.getHeaders());

        int status = metadata.getStatus() != null ? metadata.getStatus() : HttpStatus.OK.value();
        return ResponseEntity.status(status).headers(headers).body(responseBody);
    }

    private String normalizePath(String requestUri) {
        String normalizedUri = requestUri == null ? "" : requestUri.trim();
        if (normalizedUri.startsWith(API_PREFIX)) {
            normalizedUri = normalizedUri.substring(API_PREFIX.length());
        } else if (normalizedUri.startsWith("/")) {
            normalizedUri = normalizedUri.substring(1);
        }

        if (normalizedUri.isBlank()) {
            return "index";
        }

        // Basic traversal hardening for classpath resolution.
        return normalizedUri.replace("\\", "/").replace("..", "");
    }

    private String readClasspathFileOrNull(String path) {
        ClassPathResource resource = new ClassPathResource(path);
        if (!resource.exists()) {
            return null;
        }

        try {
            return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read mock body file: " + path, e);
        }
    }

    private MockResponseMetadata readMetadataOrDefault(String path) {
        ClassPathResource resource = new ClassPathResource(path);
        if (!resource.exists()) {
            return new MockResponseMetadata();
        }

        try {
            return objectMapper.readValue(resource.getInputStream(), MockResponseMetadata.class);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read mock metadata file: " + path, e);
        }
    }

    private void applyContentType(HttpHeaders headers, String contentType) {
        if (contentType == null || contentType.isBlank()) {
            headers.setContentType(MediaType.APPLICATION_JSON);
            return;
        }

        headers.setContentType(MediaType.parseMediaType(contentType));
    }

    private void applyHeaders(HttpHeaders headers, Map<String, String> customHeaders) {
        if (customHeaders == null || customHeaders.isEmpty()) {
            return;
        }

        for (Map.Entry<String, String> entry : customHeaders.entrySet()) {
            headers.set(entry.getKey(), entry.getValue());
        }
    }

    private void sleep(long delayMs) {
        try {
            Thread.sleep(delayMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
