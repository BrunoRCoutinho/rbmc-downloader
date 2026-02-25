package com.bimstart.rbmcdownloader.dto;

public class RbmcResultadoDTO {

    private String codigo;
    private String estado;
    private double distanciaKm;

    public RbmcResultadoDTO(String codigo, String estado, double distanciaKm) {
        this.codigo = codigo;
        this.estado = estado;
        this.distanciaKm = distanciaKm;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getEstado() {
        return estado;
    }

    public double getDistanciaKm() {
        return distanciaKm;
    }
}