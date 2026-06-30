package com.example.demo;

import java.util.List;

public class DashboardDTO {
    private long totalRegistros;
    private List<Registro> ultimosRegistros;

    public DashboardDTO(long totalRegistros, List<Registro> ultimosRegistros) {
        this.totalRegistros = totalRegistros;
        this.ultimosRegistros = ultimosRegistros;
    }

    public long getTotalRegistros() { return totalRegistros; }
    public List<Registro> getUltimosRegistros() { return ultimosRegistros; }
}
