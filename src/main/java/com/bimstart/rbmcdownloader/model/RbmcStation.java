package com.bimstart.rbmcdownloader.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor
public class RbmcStation {
    private String codigo;
    private double latitude;
    private double longitude;
    private String estado;
    private List<String> intervalo;
}