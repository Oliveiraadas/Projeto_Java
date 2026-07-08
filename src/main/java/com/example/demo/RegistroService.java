package com.example.demo;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVParserBuilder;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.FileReader;

@Service
public class RegistroService {

    @Autowired
    private RegistroRepository registroRepository;

    @Transactional
    public void processarArquivo() {
        try {
            Dotenv dotenv = Dotenv.load();
            String caminho_do_arquivo = dotenv.get("caminho_do_arquivo");

            registroRepository.deleteAll();

            FileReader arquivoParaDetectar = new FileReader(caminho_do_arquivo);
            BufferedReader leitorDetectar = new BufferedReader(arquivoParaDetectar);
            String primeiraLinha = leitorDetectar.readLine();
            leitorDetectar.close();

            char delimitador = descobrirDelimitador(primeiraLinha);

            FileReader arquivoReal = new FileReader(caminho_do_arquivo);
            BufferedReader bufferedReal = new BufferedReader(arquivoReal);

            com.opencsv.CSVParser parser = new CSVParserBuilder()
                    .withSeparator(delimitador)
                    .build();

            CSVReader csvReader = new CSVReaderBuilder(bufferedReal)
                    .withCSVParser(parser)
                    .build();

            String[] colunas;

            while ((colunas = csvReader.readNext()) != null) {
                if (colunas.length == 0 || (colunas.length == 1 && colunas[0].trim().isEmpty())) {
                    continue;
                }

                String col1 = (colunas.length > 0) ? colunas[0].trim() : "N/A";
                String col2 = (colunas.length > 1) ? colunas[1].trim() : "Não informado";
                String col3 = (colunas.length > 2) ? colunas[2].trim() : "0";

                Registro novoRegistro = new Registro(col1, col2, col3);
                registroRepository.save(novoRegistro);

                System.out.printf("| %-18s | %-18s | %-18s |\n", col1, col2, col3);
            }
            csvReader.close();

        } catch (Exception e) {
            System.out.println("Erro crítico ao processar o arquivo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private char descobrirDelimitador(String primeiraLinha) {
        if (primeiraLinha == null) return ';';
        long virgula = primeiraLinha.chars().filter(ch -> ch == ',').count();
        long pontoVirgula = primeiraLinha.chars().filter(ch -> ch == ';').count();
        return (virgula > pontoVirgula) ? ',' : ';';
    }
}
