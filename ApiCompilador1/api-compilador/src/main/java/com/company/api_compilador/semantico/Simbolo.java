package com.company.api_compilador.semantico;

public class Simbolo {

    private String nombre;
    private String tipo;
    private String categoria;
    private int lineaDeclaracion;
    private boolean inicializado;

    public Simbolo() {
    }

    public Simbolo(String nombre, String tipo, String categoria, int lineaDeclaracion, boolean inicializado) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.categoria = categoria;
        this.lineaDeclaracion = lineaDeclaracion;
        this.inicializado = inicializado;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public int getLineaDeclaracion() {
        return lineaDeclaracion;
    }

    public void setLineaDeclaracion(int lineaDeclaracion) {
        this.lineaDeclaracion = lineaDeclaracion;
    }

    public boolean isInicializado() {
        return inicializado;
    }

    public void setInicializado(boolean inicializado) {
        this.inicializado = inicializado;
    }
}

