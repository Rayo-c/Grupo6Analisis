package com.quickbite.quejas.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Traduce excepciones a las respuestas de error documentadas (catalogo AN02). */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiError> handleBusiness(BusinessException ex) {
        return ResponseEntity.badRequest().body(new ApiError(ex.getMessage(), HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiError(ex.getMessage(), HttpStatus.NOT_FOUND.value()));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleBadCredentials(BadCredentialsException ex) {
        // AN02 No.1 - Usuario o contrasena incorrectos.
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiError("Usuario o contrasena incorrectos.", HttpStatus.UNAUTHORIZED.value()));
    }

    @ExceptionHandler({LockedException.class, DisabledException.class})
    public ResponseEntity<ApiError> handleLocked(RuntimeException ex) {
        // AN02 No.7 - cuenta inactiva / bloqueada.
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ApiError("El usuario no cuenta con permisos para realizar esta accion, o la cuenta se encuentra inactiva.", HttpStatus.FORBIDDEN.value()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ApiError("El usuario no cuenta con permisos para realizar esta accion.", HttpStatus.FORBIDDEN.value()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
        // AN02 No.4 - El campo es obligatorio (o similar, segun el validador).
        String mensaje = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(fe -> fe.getDefaultMessage())
                .orElse("El campo es obligatorio.");
        return ResponseEntity.badRequest().body(new ApiError(mensaje, HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex) {
        return ResponseEntity.internalServerError()
                .body(new ApiError("Ocurrio un error inesperado. Intente nuevamente.", 500));
    }
}
