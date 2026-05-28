package com.company.api_compilador;

public class SolicitudEjecucion {

    private String codigo;
    private String entradas;

    public SolicitudEjecucion() {
    }

    public SolicitudEjecucion(String codigo, String entradas) {
        this.codigo = codigo;
        this.entradas = entradas;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getEntradas() {
        return entradas;
    }

    public void setEntradas(String entradas) {
        this.entradas = entradas;
    }
}
