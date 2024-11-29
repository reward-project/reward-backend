package com.outsider.reward.domain.member.command.application;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender emailSender;
    private final RedisTemplate<String, String> redisTemplate;
    private static final long VERIFICATION_CODE_TTL = 300L; // 5분

    public void sendVerificationEmail(String to) {
        String verificationCode = generateVerificationCode();
        
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("이메일 인증 코드");
        message.setText("인증 코드: " + verificationCode);
        
        emailSender.send(message);
        
        // Redis에 인증 코드 저장
        redisTemplate.opsForValue()
                .set("EMAIL_VERIFY:" + to, verificationCode, VERIFICATION_CODE_TTL, TimeUnit.SECONDS);
    }

    public boolean verifyEmail(String email, String code) {
        String storedCode = redisTemplate.opsForValue().get("EMAIL_VERIFY:" + email);
        return code.equals(storedCode);
    }

    private String generateVerificationCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }
} 