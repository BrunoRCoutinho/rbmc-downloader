package com.bimstart.rbmcdownloader.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RbmcResultadoDTO {

    private String codigo;
    private String estado;
    private double distanciaKm;
}