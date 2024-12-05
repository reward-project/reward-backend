package com.outsider.reward.global.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.outsider.reward.global.common.response.ApiResponse;
import com.outsider.reward.global.i18n.MessageUtils;
import com.outsider.reward.global.common.service.DiscordWebhookService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.PrintWriter;
import java.io.StringWriter;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    
    private final MessageUtils messageUtils;
    private final DiscordWebhookService discordWebhookService;

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
        log.error("Business Exception: {}", e.getMessage());
        String message = messageUtils.getMessage(e.getErrorCode().getMessageKey());
        return ResponseEntity
            .status(e.getErrorCode().getStatus())
            .body(ApiResponse.error(null, message));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingRequestBodyException(HttpMessageNotReadableException e) {
        log.error("Request Body Error: {}", e.getMessage());
        log.debug("Exception details:", e);
        return ResponseEntity
            .badRequest()
            .body(ApiResponse.error(null, messageUtils.getMessage("error.missing.required.field")));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException e) {
        log.error("Validation Exception: {}", e.getMessage());
        String message = e.getBindingResult().getFieldErrors().stream()
            .findFirst()
            .map(error -> error.getDefaultMessage())
            .orElse(messageUtils.getMessage("error.invalid.input"));
        return ResponseEntity
            .badRequest()
            .body(ApiResponse.error(null, message));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handleNoResourceFoundException(NoResourceFoundException ex) {
        // 404 상태 코드만 반환하고 로그는 남기지 않음
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        log.error("Unexpected Exception", e);
        
        // 스택 트레이스를 문자열로 변환
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        
        // 디스코드로 에러 알림 전송
        discordWebhookService.sendErrorMessage(
            e.getMessage(), 
            sw.toString()
        );
        
        return ResponseEntity
            .internalServerError()
            .body(ApiResponse.error(null, messageUtils.getMessage("error.server")));
    }
}