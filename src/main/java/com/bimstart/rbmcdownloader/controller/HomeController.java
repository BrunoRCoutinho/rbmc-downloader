package com.bimstart.rbmcdownloader.controller;

import com.bimstart.rbmcdownloader.service.RbmcDownloaderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Controller
@RequestMapping("/")
public class HomeController {

    private static final Logger log = LoggerFactory.getLogger(HomeController.class);

    private final RbmcDownloaderService service;

    public HomeController(RbmcDownloaderService service) {
        this.service = service;
    }

    @GetMapping
    public String home() {
        return "index";
    }

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
            List<String> arquivos = service.listarArquivosEstacaoDia(ano, dia, estacao);

            if (arquivos == null || arquivos.isEmpty()) {
                model.addAttribute("statusMessage", "⚠ Nenhum arquivo encontrado.");
                model.addAttribute("statusType", "error");
            } else {
                model.addAttribute("arquivos", arquivos);
                model.addAttribute("statusMessage",
                        "✅ " + arquivos.size() + " arquivo(s) encontrado(s).");
                model.addAttribute("statusType", "success");
            }

        } catch (IllegalArgumentException e) {
            model.addAttribute("statusMessage", "⚠ " + e.getMessage());
            model.addAttribute("statusType", "error");
        } catch (Exception e) {
            log.error("Erro ao buscar arquivos RBMC. ano={}, dia={}, estacao={}", ano, dia, estacao, e);
            model.addAttribute("statusMessage", "❌ Erro ao consultar o servidor do IBGE. Tente novamente.");
            model.addAttribute("statusType", "error");
        }

        model.addAttribute("ano", ano);
        model.addAttribute("dia", dia);
        model.addAttribute("estacao", estacao);

        return "index";
    }

    @GetMapping("/download")
    public ResponseEntity<StreamingResponseBody> downloadDia(
            @RequestParam Integer ano,
            @RequestParam Integer dia,
            @RequestParam String estacao
    ) {
        try {
            String zipName = estacao.toUpperCase() + "_" + ano + "_" + String.format("%03d", dia) + ".zip";

            StreamingResponseBody body = out ->
                    service.baixarDiaCompletoZipStream(ano, dia, estacao, out);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + zipName + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(body);

        } catch (IllegalArgumentException e) {
            log.warn("Tentativa de download com parâmetro inválido: estacao={}", estacao);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Erro no download. ano={}, dia={}, estacao={}", ano, dia, estacao, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}