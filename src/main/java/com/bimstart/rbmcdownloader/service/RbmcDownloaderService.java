package com.bimstart.rbmcdownloader.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class RbmcDownloaderService {

    private static final String BASE_URL =
            "https://geoftp.ibge.gov.br/informacoes_sobre_posicionamento_geodesico/rbmc/"
                    + "dados_RINEX3_1s/";

    private static final Path DOWNLOAD_DIR =
            Paths.get("downloads");

    // ===============================
    // STATUS
    // ===============================
    public String status() {
        return "RBMC Downloader inicializado com sucesso.";
    }

    // ===============================
    // LISTA ARQUIVOS DE UMA ESTAÇÃO
    // ===============================
    public List<String> listarArquivosEstacaoDia(int ano, int dia, String estacao) {

        List<String> arquivos = new ArrayList<>();

        String diaFormatado = String.format("%03d", dia);

        try {

            for (int hora = 0; hora < 24; hora++) {

                String horaFormatada = String.format("%02d", hora);

                String url = BASE_URL
                        + ano + "/"
                        + diaFormatado + "/"
                        + horaFormatada + "/";

                Document doc = Jsoup.connect(url)
                        .timeout(15000)
                        .get();

                Elements links = doc.select("a[href]");

                for (Element link : links) {

                    String nome = link.attr("href");

                    if (nome.toLowerCase().contains(estacao.toLowerCase())
                            && nome.endsWith(".gz")) {

                        arquivos.add(url + nome);
                    }
                }
            }

        } catch (IOException e) {

            throw new RuntimeException(
                    "Erro ao acessar RBMC: " + e.getMessage(), e);
        }

        return arquivos;
    }

    // ===============================
    // BAIXA TUDO E GERA ZIP
    // ===============================
    public Path baixarDiaCompletoZip(int ano, int dia, String estacao)
            throws IOException {

        List<String> arquivos =
                listarArquivosEstacaoDia(ano, dia, estacao);

        if (arquivos.isEmpty()) {
            throw new IOException("Nenhum arquivo encontrado para a estação informada.");
        }

        Files.createDirectories(DOWNLOAD_DIR);

        String zipName =
                estacao + "_" + ano + "_" + String.format("%03d", dia) + ".zip";

        Path zipPath = DOWNLOAD_DIR.resolve(zipName);

        try (ZipOutputStream zos =
                     new ZipOutputStream(Files.newOutputStream(zipPath))) {

            for (String urlArquivo : arquivos) {

                URI uri = URI.create(urlArquivo);

                String nome = Paths.get(uri.getPath())
                        .getFileName()
                        .toString();

                Path tempFile =
                        Files.createTempFile("rbmc_", "_" + nome);

                try (var in = uri.toURL().openStream()) {
                    Files.copy(in, tempFile, StandardCopyOption.REPLACE_EXISTING);
                }

                zos.putNextEntry(new ZipEntry(nome));
                Files.copy(tempFile, zos);
                zos.closeEntry();

                Files.deleteIfExists(tempFile);
            }
        }

        return zipPath;
    }
}
