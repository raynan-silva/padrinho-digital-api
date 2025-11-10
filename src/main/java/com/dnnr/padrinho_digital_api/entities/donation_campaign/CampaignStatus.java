package com.dnnr.padrinho_digital_api.entities.donation_campaign;

public enum CampaignStatus {
    ATIVA("ATIVA"),
    PAUSADA("PAUSADA"),
    CANCELADA("CANCELADA"),
    CONCLUIDA("CONCLUIDA");

    private String status;

    CampaignStatus(String status){
        this.status = status;
    }

    public String getStatus(){
        return status;
    }
}
