package com.example.mockserver.model;

import java.util.Map;

public class MockResponseMetadata {

    private Integer status;
    private Map<String, String> headers;
    private Long delayMs;
    private String contentType;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public Long getDelayMs() {
        return delayMs;
    }

    public void setDelayMs(Long delayMs) {
        this.delayMs = delayMs;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
