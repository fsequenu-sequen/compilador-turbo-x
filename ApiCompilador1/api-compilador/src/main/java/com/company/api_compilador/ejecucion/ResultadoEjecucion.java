/** Chimaltenango 30 de mayo 2026
Proyecto Final Compiladores
Integrantes: 
1990-23-4406	Christopher Obryan Mazariegos Crúz
1990-23-17188	Luis Miguel Vaquiax Camey
1990-23-10442	Keyner Alejandro Rivera Axpuac
1990-23-22934	Freyder José Sequén Urlao
*/
package com.company.api_compilador.ejecucion;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Modelo de respuesta para la ejecución de un programa Turbo X.
 *
 * Spring Boot convierte esta clase a JSON. Contiene el estado de ejecución,
 * mensajes para el usuario, salida generada por IMPRIMIR, errores, variables
 * finales y cantidad de instrucciones ejecutadas.
 */
public class ResultadoEjecucion {

    // true cuando el programa se ejecutó sin errores runtime.
    private boolean correcto;

    // Mensaje general que resume el resultado de la ejecución.
    private String mensaje;

    // Líneas generadas por instrucciones IMPRIMIR.
    private List<String> salida = new ArrayList<>();

    // Errores encontrados durante la interpretación.
    private List<String> errores = new ArrayList<>();

    // Estado final de las variables declaradas en el programa.
    private Map<String, ValorRuntime> variables = new LinkedHashMap<>();

    // Contador usado para saber cuánto código se ejecutó y controlar bucles.
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
