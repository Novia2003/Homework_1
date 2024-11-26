package ru.tbank.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.tbank.dto.login.LoginRequestDTO;
import ru.tbank.dto.login.TokenResponseDTO;
import ru.tbank.dto.registration.RegistrationDTO;
import ru.tbank.dto.reset.PasswordResetDTO;
import ru.tbank.service.AuthService;

@Slf4j
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
        MDC.put("username", registrationDTO.getUsername());
        log.info("Registering user");

        authService.register(registrationDTO);

        log.info("Registration successful");
        MDC.clear();

        return "Registration successful";
    }

    @PostMapping("/login")
    public TokenResponseDTO login(
            @RequestBody LoginRequestDTO loginRequestDTO
    ) {
        MDC.put("username", loginRequestDTO.getUsername());
        log.info("Logging in user");

        TokenResponseDTO tokenResponseDTO = authService.authenticate(loginRequestDTO);

        log.info("Login successful");
        MDC.clear();

        return tokenResponseDTO;
    }

    @PostMapping("/logout")
    public String logout(
            HttpServletRequest request
    ) {
        log.info("Logging out user");
        authService.logout(request);

        log.info("Logout successful");
        return "Logout successful!";
    }

    @PostMapping("/send-verification-code")
    public String sendVerificationCode(
            HttpServletRequest request
    ) {
        log.info("Sending verification code");
        authService.sendVerificationCode(request);

        log.info("Verification code sent successfully");
        return "Verification code sent successfully!";
    }

    @PostMapping("/reset-password")
    public TokenResponseDTO resetPassword(
            @RequestBody PasswordResetDTO passwordResetDTO
    ) {
        log.info("Resetting password");
        TokenResponseDTO tokenResponseDTO = authService.resetPassword(passwordResetDTO);

        log.info("Password reset successful");
        return tokenResponseDTO;
    }
}