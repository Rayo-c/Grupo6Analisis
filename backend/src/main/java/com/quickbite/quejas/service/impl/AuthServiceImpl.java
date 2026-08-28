package com.quickbite.quejas.service.impl;

import com.quickbite.quejas.dto.auth.*;
import com.quickbite.quejas.exception.BusinessException;
import com.quickbite.quejas.model.EstadoCuenta;
import com.quickbite.quejas.model.Rol;
import com.quickbite.quejas.model.TipoNotificacion;
import com.quickbite.quejas.model.Usuario;
import com.quickbite.quejas.repository.UsuarioRepository;
import com.quickbite.quejas.security.CustomUserDetails;
import com.quickbite.quejas.security.JwtUtil;
import com.quickbite.quejas.service.AuthService;
import com.quickbite.quejas.service.NotificacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.UUID;

/** CU01 Iniciar Sesion, CU02 Registrar Cliente, CU03 Recuperar Contrasena. */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final NotificacionService notificacionService;

    @Value("${app.negocio.intentos-login-bloqueo}")
    private int intentosLoginBloqueo;

    @Override
    @Transactional
    public LoginResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByCorreoIgnoreCase(request.correo())
                .orElseThrow(() -> new BusinessException("Usuario o contrasena incorrectos."));

        // FA02 CU01 - cuenta inactiva o bloqueada
        if (usuario.getEstado() == EstadoCuenta.SUSPENDIDO || usuario.getEstado() == EstadoCuenta.INACTIVO) {
            throw new BusinessException("El usuario no cuenta con permisos para realizar esta accion, o la cuenta se encuentra inactiva.");
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.correo(), request.password()));
        } catch (Exception ex) {
            // FA01 CU01 - credenciales invalidas: incrementa contador (regla de bloqueo tras 5 intentos)
            usuario.setIntentosFallidos(usuario.getIntentosFallidos() + 1);
            if (usuario.getIntentosFallidos() >= intentosLoginBloqueo) {
                usuario.setEstado(EstadoCuenta.SUSPENDIDO);
                usuario.setMotivoSuspension("Bloqueo automatico por intentos fallidos de inicio de sesion.");
            }
            usuarioRepository.save(usuario);
            throw new BusinessException("Usuario o contrasena incorrectos.");
        }

        usuario.setIntentosFallidos(0);
        usuarioRepository.save(usuario);

        String token = jwtUtil.generarToken(usuario.getCorreo(), usuario.getRol().name(), usuario.getId());
        return new LoginResponse(token, usuario.getId(), usuario.getNombreCompleto(), usuario.getCorreo(), usuario.getRol().name());
    }

    @Override
    @Transactional
    public void registrarCliente(RegistroClienteRequest request) {
        // FA03 CU02 - correo ya registrado
        if (usuarioRepository.existsByCorreoIgnoreCase(request.correo())) {
            throw new BusinessException("El correo electronico ya se encuentra registrado.");
        }

        String tokenVerificacion = UUID.randomUUID().toString();
        Usuario cliente = Usuario.builder()
                .nombreCompleto(request.nombreCompleto())
                .correo(request.correo())
                .telefono(request.telefono())
                .passwordHash(passwordEncoder.encode(request.password()))
                .rol(Rol.ROLE_CLIENTE)
                .estado(EstadoCuenta.PENDIENTE_VERIFICACION)
                .tokenVerificacion(tokenVerificacion)
                .build();
        usuarioRepository.save(cliente);

        notificacionService.enviar(cliente.getCorreo(), TipoNotificacion.VERIFICACION_CUENTA,
                "Verifica tu cuenta con el siguiente codigo: " + tokenVerificacion, null);
    }

    @Override
    @Transactional
    public void verificarCuenta(String token) {
        Usuario usuario = usuarioRepository.findByTokenVerificacion(token)
                // FA05 CU02 - enlace de verificacion expirado / invalido
                .orElseThrow(() -> new BusinessException("El enlace de recuperacion de contrasena ha expirado."));

        usuario.setEstado(EstadoCuenta.ACTIVO);
        usuario.setTokenVerificacion(null);
        usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public void recuperarPassword(RecuperarPasswordRequest request) {
        Usuario usuario = usuarioRepository.findByCorreoIgnoreCase(request.correo())
                // FA02 CU03 - correo no registrado
                .orElseThrow(() -> new BusinessException("No existen registros."));

        // RN06 / CU03: codigo numerico corto (6 digitos), mas practico de escribir que un UUID.
        String token = generarCodigoNumericoUnico(6);
        usuario.setTokenRecuperacion(token);
        usuario.setTokenRecuperacionExpira(LocalDateTime.now().plusMinutes(30));
        usuarioRepository.save(usuario);

        notificacionService.enviar(usuario.getCorreo(), TipoNotificacion.RECUPERACION_PASSWORD,
                "Tu codigo para restablecer tu contrasena es: " + token + " (valido por 30 minutos).", null);
    }

    private String generarCodigoNumericoUnico(int digitos) {
        String codigo;
        do {
            codigo = generarCodigoNumerico(digitos);
        } while (usuarioRepository.findByTokenRecuperacion(codigo).isPresent());
        return codigo;
    }

    private String generarCodigoNumerico(int digitos) {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(digitos);
        for (int i = 0; i < digitos; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        Usuario usuario = usuarioRepository.findByTokenRecuperacion(request.token())
                .orElseThrow(() -> new BusinessException("El enlace de recuperacion de contrasena ha expirado."));

        // FA03 CU03 - enlace expirado
        if (usuario.getTokenRecuperacionExpira() == null || usuario.getTokenRecuperacionExpira().isBefore(LocalDateTime.now())) {
            throw new BusinessException("El enlace de recuperacion de contrasena ha expirado.");
        }

        usuario.setPasswordHash(passwordEncoder.encode(request.nuevaPassword()));
        usuario.setTokenRecuperacion(null);
        usuario.setTokenRecuperacionExpira(null);
        usuarioRepository.save(usuario);
    }
}