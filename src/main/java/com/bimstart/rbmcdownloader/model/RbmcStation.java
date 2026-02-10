package com.bimstart.rbmcdownloader.model;

public class RbmcStation {

    private String codigo;
    private String nome;

    public RbmcStation(String codigo, String nome) {
        this.codigo = codigo;
        this.nome = nome;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNome() {
        return nome;
    }
}
