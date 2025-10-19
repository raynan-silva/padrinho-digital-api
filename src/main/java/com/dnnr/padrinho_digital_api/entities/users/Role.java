package com.dnnr.padrinho_digital_api.entities.users;

public enum Role {
    ADMIN("ADMIN"),
    GERENTE("GERENTE"),
    VOLUNTARIO("VOLUNTARIO"),
    PADRINHO("PADRINHO");

    private String role;

    Role(String role){
        this.role = role;
    }

    public String getRole(){
        return role;
    }
}
