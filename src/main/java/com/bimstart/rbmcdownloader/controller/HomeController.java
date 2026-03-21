package com.bimstart.rbmcdownloader.controller;

import com.bimstart.rbmcdownloader.service.RbmcDownloaderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.*;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.nio.file.Path;
import java.util.List;

@Controller
@RequestMapping("/") // <-- força base limpa
public class HomeController {

    private final RbmcDownloaderService service;

    public HomeController(RbmcDownloaderService service) {
        this.service = service;
    }

    @GetMapping
    public String home() {
        return "index";
    }

    // AGORA aceita GET e POST (blindado contra 405)
    @RequestMapping(value = "/buscar", method = {RequestMethod.GET, RequestMethod.POST})
    public String buscar(
            @RequestParam(required = false) Integer ano,
            @RequestParam(required = false) Integer dia,
            @RequestParam(required = false) String estacao,
            Model model
    ) {

        if (ano == null || dia == null || estacao == null || estacao.isBlank()) {
            model.addAttribute("statusMessage", "⚠ Informe todos os parâmetros.");
            model.addAttribute("statusType", "error");
            return "index";
        }

        try {

            List<String> arquivos =
                    service.listarArquivosEstacaoDia(ano, dia, estacao);

            if (arquivos == null || arquivos.isEmpty()) {
                model.addAttribute("statusMessage",
                        "⚠ Nenhum arquivo encontrado.");
                model.addAttribute("statusType", "error");
            } else {
                model.addAttribute("arquivos", arquivos);
                model.addAttribute("statusMessage",
                        "✅ " + arquivos.size() + " arquivo(s) encontrado(s).");
                model.addAttribute("statusType", "success");
            }

        } catch (Exception e) {
            model.addAttribute("statusMessage",
                    "❌ Erro: " + e.getMessage());
            model.addAttribute("statusType", "error");
        }

        model.addAttribute("ano", ano);
        model.addAttribute("dia", dia);
        model.addAttribute("estacao", estacao);

        return "index";
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadDia(
            @RequestParam Integer ano,
            @RequestParam Integer dia,
            @RequestParam String estacao
    ) {

        try {

            Path zip = service.baixarDiaCompletoZip(ano, dia, estacao);
            Resource resource = new UrlResource(zip.toUri());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + zip.getFileName() + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}