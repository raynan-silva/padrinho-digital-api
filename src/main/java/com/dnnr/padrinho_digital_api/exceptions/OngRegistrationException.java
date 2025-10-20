package com.dnnr.padrinho_digital_api.exceptions;

public class OngRegistrationException extends RuntimeException {
    public OngRegistrationException(String message) {
        super(message);
    }
    public OngRegistrationException(){
        super("Não é permitido o cadastro de ONG's ou voluntários");
    }
}
