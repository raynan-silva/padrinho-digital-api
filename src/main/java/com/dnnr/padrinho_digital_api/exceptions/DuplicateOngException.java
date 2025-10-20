package com.dnnr.padrinho_digital_api.exceptions;

public class DuplicateOngException extends RuntimeException {
    public DuplicateOngException(String message) {
        super(message);
    }

    public DuplicateOngException() {
        super("ONG já cadastrada");
    }
}
