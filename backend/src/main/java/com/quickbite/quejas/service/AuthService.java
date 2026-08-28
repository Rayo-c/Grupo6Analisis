package com.quickbite.quejas.service;

import com.quickbite.quejas.dto.auth.*;

public interface AuthService {
    LoginResponse login(LoginRequest request);                 // CU01
    void registrarCliente(RegistroClienteRequest request);      // CU02
    void verificarCuenta(String token);                         // CU02 - paso 10-11
    void recuperarPassword(RecuperarPasswordRequest request);   // CU03
    void resetPassword(ResetPasswordRequest request);           // CU03
}
