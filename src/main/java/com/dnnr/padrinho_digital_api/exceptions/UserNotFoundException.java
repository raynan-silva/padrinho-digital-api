package com.dnnr.padrinho_digital_api.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(){
        super("Usuário não encontrado");
    }
}
