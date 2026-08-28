package com.quickbite.quejas.exception;

/**
 * Excepcion de regla de negocio. El mensaje corresponde textualmente
 * a un mensaje del catalogo AN02 (Reglas de Negocio de Casos de Uso).
 */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
