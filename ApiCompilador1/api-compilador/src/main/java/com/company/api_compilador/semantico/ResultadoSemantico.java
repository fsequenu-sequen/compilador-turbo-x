/** Chimaltenango 30 de mayo 2026
Proyecto Final Compiladores
Integrantes: 
1990-23-4406	Christopher Obryan Mazariegos Crúz
1990-23-17188	Luis Miguel Vaquiax Camey
1990-23-10442	Keyner Alejandro Rivera Axpuac
1990-23-22934	Freyder José Sequén Urlao
*/
package com.company.api_compilador.semantico;

import java.util.ArrayList;
import java.util.List;

/**
 * Modelo de respuesta para la fase semántica.
 *
 * Incluye si el análisis fue correcto, un mensaje general, la lista de errores
 * encontrados y la tabla de símbolos generada durante la revisión.
 */
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
