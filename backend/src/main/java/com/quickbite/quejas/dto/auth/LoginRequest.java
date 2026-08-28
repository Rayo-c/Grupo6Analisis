package com.quickbite.quejas.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "El campo es obligatorio.") @Email(message = "El formato del correo electronico no es valido.") String correo,
        @NotBlank(message = "El campo es obligatorio.") String password
) {}
