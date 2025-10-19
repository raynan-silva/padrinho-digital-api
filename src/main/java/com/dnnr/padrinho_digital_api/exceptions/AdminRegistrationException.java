package com.dnnr.padrinho_digital_api.exceptions;

public class AdminRegistrationException extends RuntimeException{
    public AdminRegistrationException() { super("Não é permitido o cadastro de usuários admin"); }

    public AdminRegistrationException(String message) { super(message); }
}
