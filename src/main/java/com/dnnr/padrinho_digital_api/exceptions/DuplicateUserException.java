package com.dnnr.padrinho_digital_api.exceptions;

public class DuplicateUserException extends RuntimeException {
    public DuplicateUserException(String message) {
        super(message);
    }

    public DuplicateUserException() {
        super("Usuário já cadastrado");
    }
}
