package com.p4u1.email_verify.controller;

import com.p4u1.email_verify.model.VerificationRequest;
import com.p4u1.email_verify.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class VerificationController {

    @Autowired
    private EmailService emailService;

    // Show the initial email form
    @GetMapping("/")
    public String showEmailForm(Model model) {
        model.addAttribute("verificationRequest", new VerificationRequest());
        return "email-form";
    }

    // Process email submission
    @PostMapping("/send-code")
    public String sendCode(@ModelAttribute VerificationRequest request, Model model) {
        try {
            emailService.sendVerificationEmail(request.getEmail());
            model.addAttribute("email", request.getEmail());
            return "verify-code";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to send email. Please try again.");
            return "email-form";
        }
    }

    // Show the code verification form
    @GetMapping("/verify")
    public String showCodeForm(Model model) {
        model.addAttribute("verificationRequest", new VerificationRequest());
        return "verify-code";
    }

    // Process code verification
    @PostMapping("/verify-code")
    public String verifyCode(@ModelAttribute VerificationRequest request, Model model) {
        if (emailService.verifyCode(request.getEmail(), request.getCode())) {
            model.addAttribute("message", "Email verified successfully!");
            return "success";
        } else {
            model.addAttribute("error", "Invalid verification code");
            model.addAttribute("email", request.getEmail());
            return "verify-code";
        }
    }
}

