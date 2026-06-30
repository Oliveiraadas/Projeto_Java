package com.example.demo;

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

            FileReader arquivo = new FileReader(caminho_do_arquivo);
            BufferedReader lerArquivo = new BufferedReader(arquivo);

            String linha; // Cria a variável vazia para guardar o texto de cada linha.

            while ((linha = lerArquivo.readLine()) != null) {

                String[] linhas = linha.split(";");

                if (linhas.length >= 3) {
                    String col1 = linhas[0].trim();
                    String col2 = linhas[1].trim();
                    String col3 = linhas[2].trim();

                    Registro novoRegistro = new Registro(col1, col2, col3);
                    registroRepository.save(novoRegistro);

                    System.out.printf("| %-18s | %-18s | %-18s |\n", col1, col2, col3);
                }


            }
            lerArquivo.close(); //Somente para limpar a memória

        } catch (Exception e) {
            System.out.println("Erro ao criar o registro" + e.getMessage());
        }
    }


}
