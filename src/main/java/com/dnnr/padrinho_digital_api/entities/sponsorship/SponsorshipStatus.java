package com.dnnr.padrinho_digital_api.entities.sponsorship;

public enum SponsorshipStatus {
    ATIVO("ATIVO"),
    PAUSADO("PAUSADO"),
    CANCELADO("CANCELADO");

    private String status;

    SponsorshipStatus(String status){
        this.status = status;
    }

    public String getStatus(){
        return status;
    }
}