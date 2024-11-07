package ru.tbank.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
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

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final String VERIFICATION_CODE = "0000";

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    public void register(RegistrationDTO registrationDTO) {
        if (userRepository.existsByUsername(registrationDTO.getUsername())) {
            throw new UserAlreadyExistsException("User with that name has already been registered!");
        }

        UserEntity user = new UserEntity();
        user.setUsername(registrationDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));
        user.setRole(Role.USER);

        userRepository.save(user);
    }

    public TokenResponseDTO authenticate(LoginRequestDTO loginRequestDTO) {
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

        return new TokenResponseDTO(token);
    }

    public void logout(HttpServletRequest request) {
        UserEntity user = getUserByJWT(request);
        user.setToken(null);

        userRepository.save(user);
    }

    public void sendVerificationCode(HttpServletRequest request) {
        UserEntity user = getUserByJWT(request);
        user.setTimeSendingVerificationCode(Instant.now());

        userRepository.save(user);
    }

    public TokenResponseDTO resetPassword(PasswordResetDTO passwordResetDTO) {
        Optional<UserEntity> userOptional = userRepository.findByUsername(passwordResetDTO.getUsername());
        if (userOptional.isEmpty()) {
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
            return new TokenResponseDTO(token);
        } else {
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
