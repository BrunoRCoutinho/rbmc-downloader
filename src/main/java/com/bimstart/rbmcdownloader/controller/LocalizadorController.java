package com.bimstart.rbmcdownloader.controller;

import com.bimstart.rbmcdownloader.dto.RbmcResultadoDTO;
import com.bimstart.rbmcdownloader.model.RbmcStation;
import com.bimstart.rbmcdownloader.repository.RbmcRepository;
import com.bimstart.rbmcdownloader.service.LocalizadorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/localizador")
public class LocalizadorController {

    private final LocalizadorService localizadorService;
    private final RbmcRepository repository;
    private final ObjectMapper objectMapper;

    public LocalizadorController(LocalizadorService localizadorService,
                                 RbmcRepository repository,
                                 ObjectMapper objectMapper) {
        this.localizadorService = localizadorService;
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @GetMapping
    public String paginaLocalizador(
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lon,
            @RequestParam(defaultValue = "300") Double raio,
            Model model) {

        try {
            List<RbmcStation> stations = repository.findAll();
            String stationsJson = objectMapper.writeValueAsString(stations);
            model.addAttribute("stations", stationsJson);
        } catch (Exception e) {
            model.addAttribute("stations", "[]");
        }

        if (lat != null && lon != null) {
            List<RbmcResultadoDTO> resultados =
                    localizadorService.buscarProximas(lat, lon, raio);
            model.addAttribute("resultados", resultados);
            if (!resultados.isEmpty()) {
                model.addAttribute("maisProxima", resultados.get(0));
            }
        }

        model.addAttribute("raio", raio);
        return "localizador";
    }
}