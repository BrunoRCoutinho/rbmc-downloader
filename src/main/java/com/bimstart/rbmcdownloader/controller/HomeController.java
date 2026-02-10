package com.bimstart.rbmcdownloader.controller;

import com.bimstart.rbmcdownloader.service.RbmcDownloaderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.http.*;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Controller
public class HomeController {

    private final RbmcDownloaderService service;

    public HomeController(RbmcDownloaderService service) {
        this.service = service;
    }

    // ==============================
    // Página inicial
    // ==============================
    @GetMapping("/")
    public String home(Model model) {

        model.addAttribute("status", service.status());

        return "index";
    }

    // ==============================
    // Busca arquivos da estação/dia
    // ==============================
    @GetMapping("/buscar")
    public String buscar(
            @RequestParam int ano,
            @RequestParam int dia,
            @RequestParam String estacao,
            Model model
    ) {

        model.addAttribute("status", service.status());

        List<String> arquivos =
                service.listarArquivosEstacaoDia(ano, dia, estacao);

        model.addAttribute("arquivos", arquivos);

        model.addAttribute("ano", ano);
        model.addAttribute("dia", dia);
        model.addAttribute("estacao", estacao);

        return "index";
    }

    // ==============================
    // Download ZIP das 24h
    // ==============================
    @GetMapping("/download")
    public ResponseEntity<Resource> downloadDia(
            @RequestParam int ano,
            @RequestParam int dia,
            @RequestParam String estacao
    ) throws IOException {

        Path zip = service.baixarDiaCompletoZip(ano, dia, estacao);

        Resource resource = new UrlResource(zip.toUri());

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + zip.getFileName() + "\""
                )
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}
