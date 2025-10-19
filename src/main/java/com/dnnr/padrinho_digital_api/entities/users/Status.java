package com.dnnr.padrinho_digital_api.entities.users;

public enum Status {
    ATIVO("AITVO"),
    INATIVO("INATIVO"),
    PENDENTE("PENDENTE");

    private String status;

    Status(String status){
        this.status = status;
    }

    public String getStatus(){
        return status;
    }
}
