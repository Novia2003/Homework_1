package ru.tbank.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.tbank.SpringTestForAuth;
import ru.tbank.dto.login.LoginRequestDTO;
import ru.tbank.dto.login.TokenResponseDTO;
import ru.tbank.dto.registration.RegistrationDTO;
import ru.tbank.dto.reset.PasswordResetDTO;
import ru.tbank.entity.UserEntity;
import ru.tbank.repository.UserRepository;

import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


class AuthControllerTest extends SpringTestForAuth {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DirtiesContext
    public void testSuccessfulRegistration() throws Exception {
        RegistrationDTO registrationDTO = new RegistrationDTO("Slava", "1234");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDTO)))
                .andExpect(status().isOk());


        Optional<UserEntity> userOptional = userRepository.findByUsername("Slava");
        assertTrue(userOptional.isPresent());

        UserEntity user = userOptional.get();
        assertEquals("Slava", user.getUsername());
        assertEquals("USER", user.getRole().name());
        assertNotEquals("1234", user.getPassword());
    }

    @Test
    @DirtiesContext
    public void testSuccessfulLogin() throws Exception {
        RegistrationDTO registrationDTO = new RegistrationDTO("Slava", "1234");
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDTO)))
                .andExpect(status().isOk());

        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("Slava", "1234", false);
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());

        Optional<UserEntity> userOptional = userRepository.findByUsername("Slava");
        assertTrue(userOptional.isPresent());

        UserEntity user = userOptional.get();
        assertNotNull(user.getToken());
    }

    @Test
    @DirtiesContext
    public void testSuccessfulLogout() throws Exception {
        RegistrationDTO registrationDTO = new RegistrationDTO("Slava", "1234");
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDTO)))
                .andExpect(status().isOk());

        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("Slava", "1234", false);
        TokenResponseDTO tokenResponseDTO = objectMapper.readValue(
                mockMvc.perform(post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequestDTO)))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString(),
                TokenResponseDTO.class);

        mockMvc.perform(post("/api/v1/auth/logout")
                        .header("Authorization", "Bearer " + tokenResponseDTO.getToken()))
                .andExpect(status().isOk());


        Optional<UserEntity> userOptional = userRepository.findByUsername("Slava");
        assertTrue(userOptional.isPresent());

        UserEntity user = userOptional.get();
        assertNull(user.getToken());
    }

    @Test
    @DirtiesContext
    public void testSuccessfulPasswordReset() throws Exception {
        RegistrationDTO registrationDTO = new RegistrationDTO("Slava", "1234");
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDTO)))
                .andExpect(status().isOk());

        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("Slava", "1234", false);
        TokenResponseDTO tokenResponseDTO = objectMapper.readValue(
                mockMvc.perform(post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequestDTO)))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString(),
                TokenResponseDTO.class);

        mockMvc.perform(post("/api/v1/auth/send-verification-code")
                        .header("Authorization", "Bearer " + tokenResponseDTO.getToken()))
                .andExpect(status().isOk());

        PasswordResetDTO passwordResetDTO = new PasswordResetDTO("Slava", "2345", "0000");
        mockMvc.perform(post("/api/v1/auth/reset-password")
                        .header("Authorization", "Bearer " + tokenResponseDTO.getToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordResetDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());

        Optional<UserEntity> userOptional = userRepository.findByUsername("Slava");
        assertTrue(userOptional.isPresent());

        UserEntity user = userOptional.get();
        assertNotEquals("2345", user.getPassword());
        assertNotNull(user.getToken());
    }

    @Test
    @DirtiesContext
    public void testDuplicateRegistration() throws Exception {
        RegistrationDTO registrationDTO = new RegistrationDTO("testuser", "password");
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDTO)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("User with that name has already been registered!")));
    }

    @Test
    @DirtiesContext
    public void testLoginWithWrongPassword() throws Exception {
        RegistrationDTO registrationDTO = new RegistrationDTO("Slava", "1234");
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDTO)))
                .andExpect(status().isOk());

        LoginRequestDTO wrongPasswordRequest = new LoginRequestDTO("Slava", "5678", false);
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(wrongPasswordRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DirtiesContext
    public void testLogoutWithoutToken() throws Exception {
        mockMvc.perform(post("/api/v1/auth/logout")
                        .header("Authorization", "Bearer  "))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DirtiesContext
    public void testPasswordResetWithInvalidCode() throws Exception {
        RegistrationDTO registrationDTO = new RegistrationDTO("Slava", "1234");
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDTO)))
                .andExpect(status().isOk());

        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("Slava", "1234", false);
        TokenResponseDTO tokenResponseDTO = objectMapper.readValue(
                mockMvc.perform(post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequestDTO)))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString(),
                TokenResponseDTO.class);

        mockMvc.perform(post("/api/v1/auth/send-verification-code")
                        .header("Authorization", "Bearer " + tokenResponseDTO.getToken()))
                .andExpect(status().isOk());

        String invalidVerificationCode = "1111";

        PasswordResetDTO invalidCodeRequest = new PasswordResetDTO("Slava", "5678", invalidVerificationCode);
        mockMvc.perform(post("/api/v1/auth/reset-password")
                        .header("Authorization", "Bearer " + tokenResponseDTO.getToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCodeRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Verification code is incorrect")));
    }
}
