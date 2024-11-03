package ru.tbank.service;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.tbank.dto.login.LoginDTO;
import ru.tbank.dto.registration.RegistrationDTO;
import ru.tbank.dto.reset.PasswordResetDTO;
import ru.tbank.entity.Role;
import ru.tbank.entity.UserEntity;
import ru.tbank.exception.UserAlreadyExistsException;
import ru.tbank.exception.VerificationCodeException;
import ru.tbank.repository.UserRepository;

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

    public void logout(HttpServletResponse response) {
        response.setHeader("Authorization", "");
    }

    public void authenticate(LoginDTO loginDTO, HttpServletResponse response) {
        if (!userRepository.existsByUsername(loginDTO.getUsername())) {
            throw new UsernameNotFoundException("User with that name has not been registered!");
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDTO.getUsername(),
                        loginDTO.getPassword()
                )
        );

        var user = userRepository.findByUsername(loginDTO.getUsername());

        String token = jwtService.generateToken(user, loginDTO.isRememberMe());
        response.setHeader("Authorization", "Bearer " + token);
    }

    public void resetPassword(PasswordResetDTO passwordResetDTO) {
        if (!userRepository.existsByUsername(passwordResetDTO.getUsername())) {
            throw new UsernameNotFoundException("User with that name has not been registered!");
        }

        if (VERIFICATION_CODE.equals(passwordResetDTO.getVerificationCode())) {
            var user = userRepository.findByUsername(passwordResetDTO.getUsername());
            user.setPassword(passwordEncoder.encode(passwordResetDTO.getNewPassword()));
            userRepository.save(user);
        } else {
            throw new VerificationCodeException("Verification code is incorrect");
        }
    }
}
