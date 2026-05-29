package com.company.api_compilador.semantico;

import java.util.ArrayList;
import java.util.List;

public class ResultadoSemantico {

    private boolean correcto;
    private String mensaje;
    private List<ErrorSemantico> errores = new ArrayList<>();
    private List<Simbolo> tablaSimbolos = new ArrayList<>();

    public ResultadoSemantico() {
    }

    public ResultadoSemantico(boolean correcto, String mensaje, List<ErrorSemantico> errores, List<Simbolo> tablaSimbolos) {
        this.correcto = correcto;
        this.mensaje = mensaje;
        this.errores = errores;
        this.tablaSimbolos = tablaSimbolos;
    }

    public boolean isCorrecto() {
        return correcto;
    }

    public void setCorrecto(boolean correcto) {
        this.correcto = correcto;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public List<ErrorSemantico> getErrores() {
        return errores;
    }

    public void setErrores(List<ErrorSemantico> errores) {
        this.errores = errores;
    }

    public List<Simbolo> getTablaSimbolos() {
        return tablaSimbolos;
    }

    public void setTablaSimbolos(List<Simbolo> tablaSimbolos) {
        this.tablaSimbolos = tablaSimbolos;
    }
}
