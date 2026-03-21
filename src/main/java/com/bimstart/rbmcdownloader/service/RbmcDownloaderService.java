package com.bimstart.rbmcdownloader.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class RbmcDownloaderService {

    private static final Logger log = LoggerFactory.getLogger(RbmcDownloaderService.class);

    private static final String BASE_URL =
            "https://geoftp.ibge.gov.br/informacoes_sobre_posicionamento_geodesico/rbmc/"
                    + "dados_RINEX3_1s/";

    private static final ExecutorService EXECUTOR =
            Executors.newFixedThreadPool(8);

    private void validarEstacao(String estacao) {
        if (estacao == null || !estacao.matches("[A-Za-z0-9]{4}")) {
            throw new IllegalArgumentException(
                    "Código de estação inválido. Use exatamente 4 caracteres alfanuméricos (ex: PERC).");
        }
    }

    public String status() {
        return "RBMC Downloader inicializado com sucesso.";
    }

    public List<String> listarArquivosEstacaoDia(int ano, int dia, String estacao) {

        validarEstacao(estacao);

        String diaFormatado = String.format("%03d", dia);
        String estacaoLower = estacao.toLowerCase();

        List<Callable<List<String>>> tarefas = new ArrayList<>();

        for (int hora = 0; hora < 24; hora++) {
            final String horaFormatada = String.format("%02d", hora);
            final String url = BASE_URL + ano + "/" + diaFormatado + "/" + horaFormatada + "/";

            tarefas.add(() -> {
                List<String> encontrados = new ArrayList<>();
                try {
                    Document doc = Jsoup.connect(url).timeout(15000).get();
                    Elements links = doc.select("a[href]");
                    for (Element link : links) {
                        String nome = link.attr("href");
                        if (nome.toLowerCase().contains(estacaoLower) && nome.endsWith(".gz")) {
                            encontrados.add(url + nome);
                        }
                    }
                } catch (IOException e) {
                    log.debug("Sem dados em {}: {}", url, e.getMessage());
                }
                return encontrados;
            });
        }

        try {
            List<Future<List<String>>> resultados = EXECUTOR.invokeAll(tarefas, 60, TimeUnit.SECONDS);

            return resultados.stream()
                    .map(f -> {
                        try {
                            return f.get();
                        } catch (Exception e) {
                            log.warn("Tarefa falhou: {}", e.getMessage());
                            return Collections.<String>emptyList();
                        }
                    })
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Busca interrompida.", e);
        }
    }

    public void baixarDiaCompletoZipStream(int ano, int dia, String estacao, OutputStream out)
            throws IOException {

        validarEstacao(estacao);

        List<String> arquivos = listarArquivosEstacaoDia(ano, dia, estacao);

        if (arquivos.isEmpty()) {
            throw new IOException("Nenhum arquivo encontrado para a estação informada.");
        }

        try (ZipOutputStream zos = new ZipOutputStream(out)) {
            for (String urlArquivo : arquivos) {
                URI uri = URI.create(urlArquivo);
                String nome = Paths.get(uri.getPath()).getFileName().toString();
                zos.putNextEntry(new ZipEntry(nome));
                try (var in = uri.toURL().openStream()) {
                    in.transferTo(zos);
                } catch (IOException e) {
                    log.warn("Falha ao baixar {}: {}", urlArquivo, e.getMessage());
                }
                zos.closeEntry();
            }
        }
    }
}