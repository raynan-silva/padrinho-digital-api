package com.dnnr.padrinho_digital_api.entities.chat;

public enum MessageStatus {
    ENVIADA("ENVIADA"),
    ENTREGUE("ENTREGUE"),
    LIDA("LIDA");

    private String status;

    MessageStatus(String status){
        this.status = status;
    }

    public String getStatus(){
        return status;
    }
}
