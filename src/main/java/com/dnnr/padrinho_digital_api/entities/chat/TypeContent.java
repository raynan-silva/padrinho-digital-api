package com.dnnr.padrinho_digital_api.entities.chat;

public enum TypeContent {
    TEXTO("TEXTO"),
    IMAGEM("IMAGEM");

    private String type;

    TypeContent(String type){
        this.type = type;
    }

    public String getType(){
        return type;
    }
}
