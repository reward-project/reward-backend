package com.outsider.reward.domain.member.command.application;

import com.outsider.reward.domain.member.exception.MemberException;
import com.outsider.reward.domain.member.exception.MemberErrorCode;
import com.outsider.reward.domain.member.command.domain.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.RedisTemplate;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender emailSender;
    private final RedisTemplate<String, String> redisTemplate;
    private final MemberRepository memberRepository;
    private static final long VERIFICATION_CODE_TTL = 300L; // 5분

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendVerificationEmail(String email) {
        if (memberRepository.existsByBasicInfo_Email(email)) {
            throw new MemberException(MemberErrorCode.MEMBER_ALREADY_EXISTS);
        }

        String verificationCode = generateVerificationCode();
        
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(email);
        message.setSubject("이메일 인증 코드");
        message.setText("인증 코드: " + verificationCode);
        
        emailSender.send(message);
        
        // Redis에 인증 코드 저장
        redisTemplate.opsForValue()
                .set("EMAIL_VERIFY:" + email, verificationCode, VERIFICATION_CODE_TTL, TimeUnit.SECONDS);
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