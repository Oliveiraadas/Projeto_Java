package com.example.demo;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RegistroService {

    private static final Logger log = LoggerFactory.getLogger(RegistroService.class);

    @Autowired
    private RegistroRepository registroRepository;

    @Transactional
    public void processarArquivoUsuario(MultipartFile arquivo) {
        if (arquivo == null || arquivo.isEmpty()) {
            throw new IllegalArgumentException("Arquivo vazio ou nulo");
        }

        String nomeArquivo = arquivo.getOriginalFilename();
        if (nomeArquivo == null ||
                (!nomeArquivo.toLowerCase().endsWith(".csv") &&
                        !nomeArquivo.toLowerCase().endsWith(".txt"))) {
            throw new IllegalArgumentException("Apenas arquivos CSV ou TXT são permitidos");
        }

        try {
            registroRepository.deleteAll();

            String primeiraLinha;
            try (BufferedReader leitor = new BufferedReader(
                    new InputStreamReader(arquivo.getInputStream(), StandardCharsets.UTF_8))) {
                primeiraLinha = leitor.readLine();
            }

            char delimitador = descobrirDelimitador(primeiraLinha);

            CSVParser parser = new CSVParserBuilder().withSeparator(delimitador).build();

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(arquivo.getInputStream(), StandardCharsets.UTF_8));
                 CSVReader csvReader = new CSVReaderBuilder(reader)
                         .withCSVParser(parser)
                         .withSkipLines(1)
                         .build()) {

                List<Registro> listaParaSalvar = new ArrayList<>();
                String[] colunas;

                while ((colunas = csvReader.readNext()) != null) {
                    if (colunas.length == 0 || (colunas.length == 1 && colunas[0].trim().isEmpty())) {
                        continue;
                    }

                    String col1 = (colunas.length > 0) ? colunas[0].trim() : "N/A";
                    String col2 = (colunas.length > 1) ? colunas[1].trim() : "Nao informado";

                    Double col3 = 0.0;
                    if (colunas.length > 2) {
                        String col3Str = colunas[2].trim().replace(",", ".");
                        try {
                            col3 = Double.parseDouble(col3Str);
                        } catch (NumberFormatException e) {
                            log.warn("Valor invalido para coluna3: '{}', usando 0.0", col3Str);
                        }
                    }

                    listaParaSalvar.add(new Registro(col1, col2, col3));
                }

                if (!listaParaSalvar.isEmpty()) {
                    registroRepository.saveAll(listaParaSalvar);
                    log.info("Sucesso: {} registros importados.", listaParaSalvar.size());
                }
            }

        } catch (Exception e) {
            log.error("Erro ao processar upload: {}", e.getMessage(), e);
            throw new RuntimeException("Falha no processamento do arquivo CSV", e);
        }
    }

    private char descobrirDelimitador(String primeiraLinha) {
        if (primeiraLinha == null) return ';';
        long virgula = primeiraLinha.chars().filter(ch -> ch == ',').count();
        long pontoVirgula = primeiraLinha.chars().filter(ch -> ch == ';').count();
        return (virgula > pontoVirgula) ? ',' : ';';
    }

    public double somarColuna3(List<Registro> registros) {
        return registros.stream()
                .mapToDouble(Registro::getColuna3)
                .sum();
    }

    public long contarCategoriasDistintas(List<Registro> registros) {
        return registros.stream()
                .map(Registro::getColuna2)
                .distinct()
                .count();
    }

    public Map<String, Long> contagemPorColuna2(List<Registro> registros) {
        return registros.stream()
                .collect(Collectors.groupingBy(Registro::getColuna2, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(8)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
    }
}