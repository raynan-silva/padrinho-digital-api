package com.dnnr.padrinho_digital_api.entities.ong;

public enum OngStatus {
    ATIVO("AITVO"),
    INATIVO("INATIVO"),
    PENDENTE("PENDENTE");

    private String status;

    OngStatus(String status){
        this.status = status;
    }

    public String getStatus(){
        return status;
    }
}
