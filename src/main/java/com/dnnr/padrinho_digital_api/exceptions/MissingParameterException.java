package com.dnnr.padrinho_digital_api.exceptions;

public class MissingParameterException extends RuntimeException {
    public MissingParameterException(String message) {
        super(message);
    }
}
