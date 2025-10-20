package com.dnnr.padrinho_digital_api.exceptions;

public class GodfatherRegistrationException extends RuntimeException {
    public GodfatherRegistrationException(String message) {
        super(message);
    }

    public GodfatherRegistrationException() {
        super("Não é permitido o cadastro de padrinhos");
    }
}
