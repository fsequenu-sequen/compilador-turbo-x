package com.company.api_compilador;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ResultadoEjecucion {

    private boolean correcto;
    private String mensaje;
    private List<String> salida = new ArrayList<>();
    private List<String> errores = new ArrayList<>();
    private Map<String, ValorRuntime> variables = new LinkedHashMap<>();
    private int instruccionesEjecutadas;

    public ResultadoEjecucion() {
    }

    public ResultadoEjecucion(boolean correcto, String mensaje, List<String> salida,
                              List<String> errores, Map<String, ValorRuntime> variables,
                              int instruccionesEjecutadas) {
        this.correcto = correcto;
        this.mensaje = mensaje;
        this.salida = salida;
        this.errores = errores;
        this.variables = variables;
        this.instruccionesEjecutadas = instruccionesEjecutadas;
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

    public List<String> getSalida() {
        return salida;
    }

    public void setSalida(List<String> salida) {
        this.salida = salida;
    }

    public List<String> getErrores() {
        return errores;
    }

    public void setErrores(List<String> errores) {
        this.errores = errores;
    }

    public Map<String, ValorRuntime> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, ValorRuntime> variables) {
        this.variables = variables;
    }

    public int getInstruccionesEjecutadas() {
        return instruccionesEjecutadas;
    }

    public void setInstruccionesEjecutadas(int instruccionesEjecutadas) {
        this.instruccionesEjecutadas = instruccionesEjecutadas;
    }
}
