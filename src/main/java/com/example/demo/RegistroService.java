package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RegistroService {

    private static final Logger log = LoggerFactory.getLogger(RegistroService.class);

    @Autowired
    private RegistroRepository registroRepository;

    @Transactional
    public void processarArquivoUsuario(MultipartFile arquivo) {
        validarArquivo(arquivo);

        try {
            String conteudo = new String(arquivo.getBytes(), StandardCharsets.UTF_8);
            conteudo = removerBOM(conteudo);

            char delimitador = detectarDelimitador(conteudo);
            log.info("Delimitador detectado: '{}'", delimitador);

            List<String[]> todasLinhas = lerCSV(conteudo, delimitador);

            if (todasLinhas.isEmpty()) {
                log.warn("Nenhuma linha encontrada");
                return;
            }

            String[] cabecalho = todasLinhas.get(0);
            List<String[]> dados = todasLinhas.subList(1, todasLinhas.size());

            List<Registro> registros = new ArrayList<>();
            for (String[] linha : dados) {
                if (linha.length == 0) continue;
                registros.add(new Registro(cabecalho, linha));
            }

            if (!registros.isEmpty()) {
                registroRepository.deleteAll();
                registroRepository.saveAll(registros);
                log.info("Sucesso: {} registros importados de {} colunas.",
                        registros.size(), cabecalho.length);
            }

        } catch (Exception e) {
            log.error("Erro: {}", e.getMessage(), e);
            throw new RuntimeException("Falha no processamento", e);
        }
    }

    private char detectarDelimitador(String conteudo) {
        if (conteudo == null || conteudo.isEmpty()) return ',';

        String primeiraLinha = conteudo.split("\n")[0];

        String linhaLimpa = primeiraLinha.replaceAll("\"[^\"]*\"", "");

        long virgulas = linhaLimpa.chars().filter(c -> c == ',').count();
        long pontoVirgulas = linhaLimpa.chars().filter(c -> c == ';').count();

        log.info("Vírgulas: {}, Ponto-e-vírgulas: {}", virgulas, pontoVirgulas);

        return virgulas >= pontoVirgulas ? ',' : ';';
    }

    private List<String[]> lerCSV(String conteudo, char delimitador) {
        List<String[]> linhas = new ArrayList<>();
        String[] linhasBrutas = conteudo.split("\n");

        for (String linhaBruta : linhasBrutas) {
            String linha = linhaBruta.trim();
            if (linha.isEmpty()) continue;

            if (linha.endsWith(";")) {
                linha = linha.substring(0, linha.length() - 1);
            }

            String[] colunas = dividirLinha(linha, delimitador);
            if (colunas.length > 0) {
                linhas.add(colunas);
            }
        }

        return linhas;
    }

    private String[] dividirLinha(String linha, char delimitador) {
        List<String> partes = new ArrayList<>();
        StringBuilder campo = new StringBuilder();
        boolean dentroAspas = false;

        for (int i = 0; i < linha.length(); i++) {
            char c = linha.charAt(i);

            if (c == '"') {
                if (dentroAspas && i + 1 < linha.length() && linha.charAt(i + 1) == '"') {
                    campo.append('"');
                    i++;
                } else {
                    dentroAspas = !dentroAspas;
                }
            } else if (c == delimitador && !dentroAspas) {
                partes.add(campo.toString().trim());
                campo.setLength(0);
            } else {
                campo.append(c);
            }
        }
        partes.add(campo.toString().trim());

        for (int i = 0; i < partes.size(); i++) {
            String valor = partes.get(i);
            if (valor.startsWith("\"") && valor.endsWith("\"")) {
                valor = valor.substring(1, valor.length() - 1);
            }
            partes.set(i, valor);
        }

        return partes.toArray(new String[0]);
    }

    private String removerBOM(String conteudo) {
        if (conteudo.startsWith("\uFEFF")) {
            return conteudo.substring(1);
        }
        return conteudo;
    }

    private void validarArquivo(MultipartFile arquivo) {
        if (arquivo == null || arquivo.isEmpty()) {
            throw new IllegalArgumentException("Arquivo vazio ou nulo");
        }

        String nome = arquivo.getOriginalFilename();
        if (nome == null) return;

        String lower = nome.toLowerCase();
        if (!lower.endsWith(".csv") && !lower.endsWith(".txt")) {
            throw new IllegalArgumentException("Apenas arquivos CSV ou TXT são permitidos");
        }
    }

    public double somarColuna3(List<Registro> registros) {
        if (registros == null || registros.isEmpty()) return 0.0;
        return registros.stream()
                .mapToDouble(reg -> reg.getValorNumerico() != null ? reg.getValorNumerico() : 0.0)
                .sum();
    }

    public long contarCategoriasDistintas(List<Registro> registros) {
        if (registros == null || registros.isEmpty()) return 0;
        return registros.stream()
                .map(Registro::getDescricao)
                .filter(desc -> desc != null && !desc.isEmpty())
                .distinct()
                .count();
    }

    public Map<String, Long> contagemPorColuna2(List<Registro> registros) {
        if (registros == null || registros.isEmpty()) {
            return new LinkedHashMap<>();
        }
        return registros.stream()
                .filter(reg -> reg.getDescricao() != null && !reg.getDescricao().isEmpty())
                .collect(Collectors.groupingBy(Registro::getDescricao, Collectors.counting()))
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