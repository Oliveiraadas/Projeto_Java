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
    private String coluna3;

    public Registro() {}

    public Registro(String coluna1, String coluna2, String coluna3) {
        this.coluna1 = coluna1;
        this.coluna2 = coluna2;
        this.coluna3 = coluna3;

    }

    public String getColuna1() {
        return coluna1;
    }

    public void setColuna1(String coluna1) {
        this.coluna1 = coluna1;
    }

    public String getColuna2() {
        return coluna2;
    }

    public void setColuna2(String coluna2) {
        this.coluna2 = coluna2;
    }

    public String getColuna3() {
        return coluna3;
    }

    public void setColuna3(String coluna3) {
        this.coluna3 = coluna3;
    }
}
