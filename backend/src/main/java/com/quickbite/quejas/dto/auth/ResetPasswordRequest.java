package com.quickbite.quejas.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ResetPasswordRequest(
        @NotBlank(message = "El campo es obligatorio.") String token,
        @NotBlank(message = "El campo es obligatorio.")
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,}$",
                message = "La contrasena no cumple con los requisitos minimos de seguridad."
        )
        String nuevaPassword
) {}
