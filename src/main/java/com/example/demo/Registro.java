package com.example.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import java.util.*;

@Entity
public class Registro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String cabecalhoJson;

    @Column(columnDefinition = "TEXT")
    private String dadosJson;

    private String identificador;
    private String descricao;
    private Double valorNumerico;

    private static final ObjectMapper mapper = new ObjectMapper();

    public Registro() {}

    public Registro(String[] cabecalho, String[] dados) {
        try {
            this.cabecalhoJson = mapper.writeValueAsString(cabecalho != null ? cabecalho : new String[0]);
            this.dadosJson = mapper.writeValueAsString(dados != null ? dados : new String[0]);

            if (dados != null && dados.length > 0) {
                this.identificador = dados.length > 0 ? limpar(dados[0]) : "N/A";
                this.descricao = dados.length > 1 ? limpar(dados[1]) : "N/A";
                this.valorNumerico = extrairNumero(dados);
            } else {
                this.identificador = "N/A";
                this.descricao = "N/A";
                this.valorNumerico = 0.0;
            }

        } catch (JsonProcessingException e) {
            this.cabecalhoJson = "[]";
            this.dadosJson = "[]";
            this.identificador = "N/A";
            this.descricao = "N/A";
            this.valorNumerico = 0.0;
        }
    }

    public String getColuna2() {
        return this.descricao;
    }

    public Double getColuna3() {
        return this.valorNumerico;
    }

    public String[] getCabecalho() {
        try {
            return mapper.readValue(cabecalhoJson, String[].class);
        } catch (JsonProcessingException e) {
            return new String[0];
        }
    }

    public String[] getDados() {
        try {
            return mapper.readValue(dadosJson, String[].class);
        } catch (JsonProcessingException e) {
            return new String[0];
        }
    }

    public List<String> getDadosComoLista() {
        return Arrays.asList(getDados());
    }

    public String getColuna(int indice) {
        String[] dados = getDados();
        if (indice >= 0 && indice < dados.length) {
            return dados[indice];
        }
        return "N/A";
    }

    public String getColunaPorNome(String nome) {
        String[] cabecalho = getCabecalho();
        String[] dados = getDados();
        for (int i = 0; i < cabecalho.length && i < dados.length; i++) {
            if (cabecalho[i].equalsIgnoreCase(nome.trim())) {
                return dados[i];
            }
        }
        return "N/A";
    }

    public Map<String, String> getDadosComoMap() {
        Map<String, String> mapa = new LinkedHashMap<>();
        String[] cabecalho = getCabecalho();
        String[] dados = getDados();
        for (int i = 0; i < Math.min(cabecalho.length, dados.length); i++) {
            mapa.put(cabecalho[i], dados[i]);
        }
        return mapa;
    }

    public int getQuantidadeColunas() {
        String[] dados = getDados();
        return dados != null ? dados.length : 0;
    }

    public String getIdentificador() { return identificador; }
    public String getDescricao() { return descricao; }
    public Double getValorNumerico() { return valorNumerico; }

    private String limpar(String valor) {
        if (valor == null) return "";
        valor = valor.trim();
        if (valor.startsWith("\"") && valor.endsWith("\"")) {
            valor = valor.substring(1, valor.length() - 1);
        }
        return valor;
    }

    private Double extrairNumero(String[] dados) {
        for (String valor : dados) {
            try {
                String limpo = valor.trim().replace(",", ".");
                if (limpo.matches("-?\\d+(\\.\\d+)?")) {
                    return Double.parseDouble(limpo);
                }
            } catch (NumberFormatException e) {
            }
        }
        return 0.0;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCabecalhoJson() { return cabecalhoJson; }
    public void setCabecalhoJson(String cabecalhoJson) { this.cabecalhoJson = cabecalhoJson; }

    public String getDadosJson() { return dadosJson; }
    public void setDadosJson(String dadosJson) { this.dadosJson = dadosJson; }

    public void setIdentificador(String identificador) { this.identificador = identificador; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public void setValorNumerico(Double valorNumerico) { this.valorNumerico = valorNumerico; }

    @Override
    public String toString() {
        return "Registro{" +
                "id=" + id +
                ", identificador='" + identificador + '\'' +
                ", descricao='" + descricao + '\'' +
                ", valorNumerico=" + valorNumerico +
                ", colunas=" + getQuantidadeColunas() +
                '}';
    }
}