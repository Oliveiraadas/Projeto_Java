package com.example.demo;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class RegistroService {

    @Autowired
    private RegistroRepository registroRepository;

    @Transactional
    public void processarArquivoUsuario(MultipartFile arquivo) {
        if (arquivo == null || arquivo.isEmpty()) {
            System.out.println("Erro: O arquivo enviado está vazio.");
            return;
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
                 CSVReader csvReader = new CSVReaderBuilder(reader).withCSVParser(parser).build()) {

                String[] colunas;
                List<Registro> listaParaSalvar = new ArrayList<>();
                boolean ehPrimeiraLinha = true;

                while ((colunas = csvReader.readNext()) != null) {
                    if (colunas.length == 0 || (colunas.length == 1 && colunas[0].trim().isEmpty())) {
                        continue;
                    }

                    if (ehPrimeiraLinha) {
                        ehPrimeiraLinha = false;
                        continue;
                    }

                    String col1 = (colunas.length > 0) ? colunas[0].trim() : "N/A";
                    String col2 = (colunas.length > 1) ? colunas[1].trim() : "Não informado";
                    String col3 = (colunas.length > 2) ? colunas[2].trim() : "0";

                    listaParaSalvar.add(new Registro(col1, col2, col3));
                }

                if (!listaParaSalvar.isEmpty()) {
                    registroRepository.saveAll(listaParaSalvar);
                    System.out.println("Sucesso: " + listaParaSalvar.size() + " registros importados via upload.");
                }
            }

        } catch (Exception e) {
            System.out.println("Erro crítico ao processar upload: " + e.getMessage());
        }
    }

    private char descobrirDelimitador(String primeiraLinha) {
        if (primeiraLinha == null) return ';';
        long virgula = primeiraLinha.chars().filter(ch -> ch == ',').count();
        long pontoVirgula = primeiraLinha.chars().filter(ch -> ch == ';').count();
        return (virgula > pontoVirgula) ? ',' : ';';
    }
}
