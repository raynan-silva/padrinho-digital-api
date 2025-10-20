package com.dnnr.padrinho_digital_api.entities.users;

public enum UserStatus {
    ATIVO("AITVO"),
    INATIVO("INATIVO"),
    PENDENTE("PENDENTE");

    private String status;

    UserStatus(String status){
        this.status = status;
    }

    public String getStatus(){
        return status;
    }
}
