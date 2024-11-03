package ru.tbank.dto.login;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginDTO {

    @NotNull
    private String username;

    @NotNull
    private String password;

    @NotNull
    private boolean rememberMe;
}
