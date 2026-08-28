package com.quickbite.quejas.exception;

/** AN02 No.2 - No existen registros. */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
