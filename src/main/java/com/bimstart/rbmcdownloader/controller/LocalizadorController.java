package com.bimstart.rbmcdownloader.controller;

import com.bimstart.rbmcdownloader.dto.RbmcResultadoDTO;
import com.bimstart.rbmcdownloader.service.LocalizadorService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/localizador")
public class LocalizadorController {

    private final LocalizadorService localizadorService;

    public LocalizadorController(LocalizadorService localizadorService) {
        this.localizadorService = localizadorService;
    }

    @GetMapping
    public String paginaLocalizador(
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lon,
            @RequestParam(defaultValue = "300") Double raio,
            Model model) {

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