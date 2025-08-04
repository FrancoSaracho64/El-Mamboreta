package com.mamboreta.backend.service.exception;

/**
 * Excepción personalizada para errores de negocio
 */
public class BusinessException extends RuntimeException {
    
    public BusinessException(String message) {
        super(message);
    }
    
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
} 