package com.quickbite.quejas.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/** CU02 - Registrar Cliente (autorregistro). RN06 - password: min 8, mayuscula, numero, especial. */
public record RegistroClienteRequest(
        @NotBlank(message = "El campo es obligatorio.") String nombreCompleto,
        @NotBlank(message = "El campo es obligatorio.") @Email(message = "El formato del correo electronico no es valido.") String correo,
        String telefono,
        @NotBlank(message = "El campo es obligatorio.")
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,}$",
                message = "La contrasena no cumple con los requisitos minimos de seguridad."
        )
        String password
) {}
