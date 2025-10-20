package com.dnnr.padrinho_digital_api.exceptions;

public class ExpiredTokenException extends RuntimeException {
    public ExpiredTokenException(String message) {
        super(message);
    }

    public ExpiredTokenException() {
        super("Token expirado");
    }
}
