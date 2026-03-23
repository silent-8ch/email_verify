package com.p4u1.email_verify.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // Store verification codes in memory (email -> code)
    private Map<String, String> verificationCodes = new ConcurrentHashMap<>();

    // Generate a random 6-digit code
    public String generateCode() {
        return String.format("%06d", new Random().nextInt(1000000));
    }

    // Send verification email
    public void sendVerificationEmail(String email) {
        String code = generateCode();
        verificationCodes.put(email, code);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Email Verification Code");
        message.setText("Your verification code is: " + code);
        message.setFrom("noreply@emailverify.com");

        mailSender.send(message);
    }

    // Verify the code
    public boolean verifyCode(String email, String code) {
        String storedCode = verificationCodes.get(email);
        if (storedCode != null && storedCode.equals(code)) {
            verificationCodes.remove(email); // Clean up after verification
            return true;
        }
        return false;
    }
}

