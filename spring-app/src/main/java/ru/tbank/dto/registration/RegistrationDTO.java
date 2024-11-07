package ru.tbank.dto.registration;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationDTO {

    @NotNull
    private String username;

    @NotNull
    private String password;
}
