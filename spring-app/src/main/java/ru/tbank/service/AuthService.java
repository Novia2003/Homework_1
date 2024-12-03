package ru.tbank.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.tbank.dto.login.LoginRequestDTO;
import ru.tbank.dto.login.TokenResponseDTO;
import ru.tbank.dto.registration.RegistrationDTO;
import ru.tbank.dto.reset.PasswordResetDTO;
import ru.tbank.entity.Role;
import ru.tbank.entity.UserEntity;
import ru.tbank.exception.UserAlreadyExistsException;
import ru.tbank.exception.VerificationCodeException;
import ru.tbank.repository.UserRepository;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private static final String VERIFICATION_CODE = "0000";

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    public void register(RegistrationDTO registrationDTO) {
        log.info("Starting registration process");
        if (userRepository.existsByUsername(registrationDTO.getUsername())) {
            log.warn("User already exists");
            throw new UserAlreadyExistsException("User with that name has already been registered!");
        }

        UserEntity user = new UserEntity();
        user.setUsername(registrationDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));
        user.setRole(Role.USER);

        userRepository.save(user);
        log.info("Registration successful");
    }

    public TokenResponseDTO authenticate(LoginRequestDTO loginRequestDTO) {
        log.info("Starting authentication process");
        Optional<UserEntity> userOptional = userRepository.findByUsername(loginRequestDTO.getUsername());
        if (userOptional.isEmpty()) {
            throw new UsernameNotFoundException("User with that name has not been registered!");
        }

        UserEntity user = userOptional.get();

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDTO.getUsername(),
                        loginRequestDTO.getPassword()
                )
        );

        String token = jwtService.generateToken(user, loginRequestDTO.isRememberMe());
        user.setToken(token);
        userRepository.save(user);

        log.info("Authentication successful");
        return new TokenResponseDTO(token);
    }

    public void logout(HttpServletRequest request) {
        log.info("Starting logout process");
        UserEntity user = getUserByJWT(request);
        user.setToken(null);

        userRepository.save(user);
        log.info("Logout successful");
    }

    public void sendVerificationCode(HttpServletRequest request) {
        log.info("Starting process to send verification code");
        UserEntity user = getUserByJWT(request);
        user.setTimeSendingVerificationCode(Instant.now());

        userRepository.save(user);
        log.info("Verification code sent successfully");
    }

    public TokenResponseDTO resetPassword(PasswordResetDTO passwordResetDTO) {
        log.info("Starting password reset process");
        Optional<UserEntity> userOptional = userRepository.findByUsername(passwordResetDTO.getUsername());
        if (userOptional.isEmpty()) {
            log.warn("User not found");
            throw new UsernameNotFoundException("User with that name has not been registered!");
        }

        UserEntity user = userOptional.get();

        Instant currentInstant = Instant.now();

        Duration duration = Duration.between(user.getTimeSendingVerificationCode(), currentInstant);

        if (Math.abs(duration.toMinutes()) <= 1 &&
                VERIFICATION_CODE.equals(passwordResetDTO.getVerificationCode())) {
            user.setTimeSendingVerificationCode(null);
            user.setPassword(passwordEncoder.encode(passwordResetDTO.getNewPassword()));

            String token = jwtService.generateToken(user, false);
            user.setToken(token);

            userRepository.save(user);
            log.info("Password reset successful");
            return new TokenResponseDTO(token);
        } else {
            log.warn("Verification code is incorrect");
            throw new VerificationCodeException("Verification code is incorrect");
        }
    }

    private UserEntity getUserByJWT(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        String jwt = authHeader.substring(7);
        String username = jwtService.extractUsername(jwt);

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
