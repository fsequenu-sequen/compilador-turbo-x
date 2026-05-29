package com.company.api_compilador.semantico;

public class ErrorSemantico {

    private int linea;
    private String lexema;
    private String descripcion;

    public ErrorSemantico() {
    }

    public ErrorSemantico(int linea, String lexema, String descripcion) {
        this.linea = linea;
        this.lexema = lexema;
        this.descripcion = descripcion;
    }

    public int getLinea() {
        return linea;
    }

    public void setLinea(int linea) {
        this.linea = linea;
    }

    public String getLexema() {
        return lexema;
    }

    public void setLexema(String lexema) {
        this.lexema = lexema;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
