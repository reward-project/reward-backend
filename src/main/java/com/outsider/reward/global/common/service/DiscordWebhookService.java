package com.outsider.reward.global.common.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiscordWebhookService {

    @Value("${discord.webhook.url}")
    private String webhookUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public void sendErrorMessage(String errorMessage, String stackTrace) {
        try {
            Map<String, Object> message = new HashMap<>();
            message.put("content", "🚨 에러 발생!");
            
            Map<String, Object> embed = new HashMap<>();
            embed.put("title", "에러 상세 정보");
            embed.put("color", 16711680); // 빨간색
            embed.put("description", "```\n" + errorMessage + "\n```");

            // 스택트레이스가 너무 길면 잘라내기
            String truncatedStackTrace = stackTrace.length() > 1000 
                ? stackTrace.substring(0, 1000) + "..." 
                : stackTrace;

            Map<String, String> field = new HashMap<>();
            field.put("name", "Stack Trace");
            field.put("value", "```\n" + truncatedStackTrace + "\n```");
            field.put("inline", "false");

            embed.put("fields", new Object[]{field});
            message.put("embeds", new Object[]{embed});

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> request = new HttpEntity<>(
                objectMapper.writeValueAsString(message), 
                headers
            );

            restTemplate.postForEntity(webhookUrl, request, String.class);
            
        } catch (Exception e) {
            log.error("Discord webhook 전송 실패", e);
        }
    }
} 