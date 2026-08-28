package com.quickbite.quejas.exception;

import java.time.LocalDateTime;

public record ApiError(String mensaje, int status, LocalDateTime fecha) {
    public ApiError(String mensaje, int status) {
        this(mensaje, status, LocalDateTime.now());
    }
}
