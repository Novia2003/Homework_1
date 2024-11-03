package ru.tbank.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.tbank.dto.login.LoginDTO;
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
    public String login(
            @RequestBody LoginDTO loginDTO,
            HttpServletResponse response
    ) {
        authService.authenticate(loginDTO, response);
        return "Login successful";
    }

    @PostMapping("/logout")
    public String logout(HttpServletResponse response) {
        authService.logout(response);
        return "Logout successful!";
    }

    @PostMapping("/reset-password")
    public String resetPassword(
            @RequestBody PasswordResetDTO passwordResetDTO
    ) {
        authService.resetPassword(passwordResetDTO);
        return "Password reset successful!";
    }
}