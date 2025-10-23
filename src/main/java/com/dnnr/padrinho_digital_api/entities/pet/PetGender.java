package com.dnnr.padrinho_digital_api.entities.pet;

public enum PetGender {
    MACHO ("MACHO"),
    FEMEA ("FEMEA");

    private String gender;

    PetGender(String gender){
        this.gender = gender;
    }

    public String getStatus(){
        return gender;
    }
}
