package ru.tbank.dto.reset;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PasswordResetDTO {

    @NotNull
    private String username;

    @NotNull
    private String newPassword;

    @NotNull
    private String verificationCode;
}
