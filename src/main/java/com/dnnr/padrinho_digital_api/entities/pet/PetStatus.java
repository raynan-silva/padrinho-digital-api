package com.dnnr.padrinho_digital_api.entities.pet;

public enum PetStatus {
    APADRINHAVEL ("APADRINHAVEL"),
    ADOTADO ("ADOTADO"),
    INDISPONIVEL ("INDISPONIVEL");

    private String status;

    PetStatus(String status){
        this.status = status;
    }

    public String getStatus(){
        return status;
    }
}
