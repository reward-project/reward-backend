package com.outsider.reward.global.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.stream.Collectors;

@Component
@Slf4j
public class RequestLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
        
        long startTime = System.currentTimeMillis();
        
        // 요청 로깅
        logRequest(requestWrapper);
        
        try {
            filterChain.doFilter(requestWrapper, responseWrapper);
        } finally {
            // 응답 로깅
            logResponse(responseWrapper, System.currentTimeMillis() - startTime);
            responseWrapper.copyBodyToResponse();
        }
    }
    
    private void logRequest(ContentCachingRequestWrapper request) throws IOException {
        String uri = request.getRequestURI();
        String method = request.getMethod();
        String queryString = request.getQueryString();
        String remoteAddr = request.getRemoteAddr();
        
        StringBuilder logMessage = new StringBuilder();
        logMessage.append("\n>>> REQUEST");
        logMessage.append("\n>>> Method: ").append(method);
        logMessage.append("\n>>> URI: ").append(uri);
        logMessage.append("\n>>> Query: ").append(queryString);
        logMessage.append("\n>>> Remote Address: ").append(remoteAddr);
        
        // 헤더 로깅
        logMessage.append("\n>>> Headers:");
        Collections.list(request.getHeaderNames())
                .forEach(headerName -> 
                    logMessage.append("\n>>>   ")
                            .append(headerName)
                            .append(": ")
                            .append(request.getHeader(headerName)));
        
        // 요청 본문 로깅 (JSON인 경우에만)
        if (isJsonContent(request.getContentType())) {
            String requestBody = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
            if (!requestBody.isEmpty()) {
                logMessage.append("\n>>> Body: ").append(requestBody);
            }
        }
        
        log.info(logMessage.toString());
    }
    
    private void logResponse(ContentCachingResponseWrapper response, long duration) throws IOException {
        StringBuilder logMessage = new StringBuilder();
        logMessage.append("\n<<< RESPONSE");
        logMessage.append("\n<<< Status: ").append(response.getStatus());
        logMessage.append("\n<<< Duration: ").append(duration).append("ms");
        
        // 응답 헤더 로깅
        logMessage.append("\n<<< Headers:");
        response.getHeaderNames()
                .forEach(headerName ->
                    logMessage.append("\n<<<   ")
                            .append(headerName)
                            .append(": ")
                            .append(response.getHeader(headerName)));
        
        // 응답 본문 로깅 (JSON인 경우에만)
        if (isJsonContent(response.getContentType())) {
            String responseBody = new String(response.getContentAsByteArray(), StandardCharsets.UTF_8);
            if (!responseBody.isEmpty()) {
                logMessage.append("\n<<< Body: ").append(responseBody);
            }
        }
        
        log.info(logMessage.toString());
    }
    
    private boolean isJsonContent(String contentType) {
        return contentType != null && 
               (contentType.contains(MediaType.APPLICATION_JSON_VALUE) || 
                contentType.contains("application/json"));
    }
} 