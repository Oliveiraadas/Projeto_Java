package com.example.demo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Registro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String coluna1;
    private String coluna2;
    private Double coluna3;

    public Registro() {}

    public Registro(String coluna1, String coluna2, Double coluna3) {
        this.coluna1 = coluna1;
        this.coluna2 = coluna2;
        this.coluna3 = coluna3;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getColuna1() { return coluna1; }
    public void setColuna1(String coluna1) { this.coluna1 = coluna1; }

    public String getColuna2() { return coluna2; }
    public void setColuna2(String coluna2) { this.coluna2 = coluna2; }

    public Double getColuna3() { return coluna3; }
    public void setColuna3(Double coluna3) { this.coluna3 = coluna3; }
}