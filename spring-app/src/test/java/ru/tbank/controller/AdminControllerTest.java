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
import ru.tbank.entity.Role;
import ru.tbank.entity.UserEntity;
import ru.tbank.repository.UserRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AdminControllerTest extends SpringTestForAuth {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DirtiesContext
    void shouldAllowAccessForAdminRole() throws Exception {
        RegistrationDTO registrationDTO = new RegistrationDTO("Admin", "1234");
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDTO)))
                .andExpect(status().isOk());

        UserEntity user = userRepository.findByUsername(registrationDTO.getUsername()).get();
        user.setRole(Role.ADMIN);
        userRepository.save(user);

        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("Admin", "1234", false);
        TokenResponseDTO tokenResponseDTO = objectMapper.readValue(
                mockMvc.perform(post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequestDTO)))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString(),
                TokenResponseDTO.class);

        mockMvc.perform(get("/api/v1/admin")
                        .header("Authorization", "Bearer " + tokenResponseDTO.getToken()))
                .andExpect(status().isOk())
                .andExpect(content().string("This is admin info"));
    }

    @Test
    @DirtiesContext
    void shouldDenyAccessForNonAdminRole() throws Exception {
        RegistrationDTO registrationDTO = new RegistrationDTO("Admin", "1234");
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDTO)))
                .andExpect(status().isOk());

        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("Admin", "1234", false);
        TokenResponseDTO tokenResponseDTO = objectMapper.readValue(
                mockMvc.perform(post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequestDTO)))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString(),
                TokenResponseDTO.class);

        mockMvc.perform(get("/api/v1/admin")
                        .header("Authorization", "Bearer " + tokenResponseDTO.getToken()))
                .andExpect(status().isForbidden());
    }
}
