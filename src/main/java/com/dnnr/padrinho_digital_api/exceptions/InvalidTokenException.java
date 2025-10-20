package com.dnnr.padrinho_digital_api.exceptions;

public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String message) {
        super(message);
    }

    public InvalidTokenException(){
        super("Token inválido");
    }
}
