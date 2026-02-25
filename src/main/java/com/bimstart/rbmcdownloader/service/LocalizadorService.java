package com.bimstart.rbmcdownloader.service;

import com.bimstart.rbmcdownloader.dto.RbmcResultadoDTO;
import com.bimstart.rbmcdownloader.model.RbmcStation;
import com.bimstart.rbmcdownloader.repository.RbmcRepository;
import com.bimstart.rbmcdownloader.util.DistanceCalculator;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class LocalizadorService {

    private final RbmcRepository repository;

    public LocalizadorService(RbmcRepository repository) {
        this.repository = repository;
    }

    public List<RbmcResultadoDTO> buscarProximas(double lat, double lon, double raioKm) {

        List<RbmcStation> todas = repository.findAll();

        return todas.stream()
                .map(estacao -> {
                    double distancia = DistanceCalculator.haversine(
                            lat,
                            lon,
                            estacao.getLatitude(),
                            estacao.getLongitude()
                    );

                    return new RbmcResultadoDTO(
                            estacao.getCodigo(),
                            estacao.getEstado(),
                            distancia
                    );
                })
                .filter(r -> r.getDistanciaKm() <= raioKm)
                .sorted(Comparator.comparing(RbmcResultadoDTO::getDistanciaKm))
                .toList();
    }
}