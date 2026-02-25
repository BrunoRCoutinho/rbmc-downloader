package com.bimstart.rbmcdownloader.repository;

import com.bimstart.rbmcdownloader.model.RbmcStation;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;

import jakarta.annotation.PostConstruct;
import java.io.InputStream;
import java.util.List;

@Repository
public class RbmcRepository {

    private List<RbmcStation> stations;

    @PostConstruct
    public void loadStations() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream inputStream = new ClassPathResource("rbmc_stations.json").getInputStream();
            stations = mapper.readValue(inputStream, new TypeReference<List<RbmcStation>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Erro ao carregar estações RBMC", e);
        }
    }

    public List<RbmcStation> findAll() {
        return stations;
    }
}