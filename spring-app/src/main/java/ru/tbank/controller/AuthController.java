package ru.tbank.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.tbank.dto.login.LoginRequestDTO;
import ru.tbank.dto.login.TokenResponseDTO;
import ru.tbank.dto.registration.RegistrationDTO;
import ru.tbank.dto.reset.PasswordResetDTO;
import ru.tbank.service.AuthService;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public String register(
            @Valid
            @RequestBody
            RegistrationDTO registrationDTO
    ) {
        authService.register(registrationDTO);
        return "Registration successful";
    }

    @PostMapping("/login")
    public TokenResponseDTO login(
            @RequestBody LoginRequestDTO loginRequestDTO
    ) {
         return authService.authenticate(loginRequestDTO);
    }

    @PostMapping("/logout")
    public String logout(
            HttpServletRequest request
    ) {
        authService.logout(request);
        return "Logout successful!";
    }

    @PostMapping("/send-verification-code")
    public String sendVerificationCode(
            HttpServletRequest request
    ) {
        authService.sendVerificationCode(request);
        return "Verification code sent successfully!";
    }

    @PostMapping("/reset-password")
    public TokenResponseDTO resetPassword(
            @RequestBody PasswordResetDTO passwordResetDTO
    ) {
        return authService.resetPassword(passwordResetDTO);
    }
}