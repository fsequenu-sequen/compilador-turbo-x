/** Chimaltenango 30 de mayo 2026
Proyecto Final Compiladores
Integrantes: 
1990-23-4406	Christopher Obryan Mazariegos Crúz
1990-23-17188	Luis Miguel Vaquiax Camey
1990-23-10442	Keyner Alejandro Rivera Axpuac
1990-23-22934	Freyder José Sequén Urlao
*/
package com.company.api_compilador.sintactico;

import java.util.ArrayList;
import java.util.List;

/**
 * Objeto de respuesta para el análisis sintáctico.
 * Spring Boot lo convierte automáticamente a JSON.
 */
public class ResultadoSintactico {

    private boolean correcto;
    private String mensaje;
    private List<String> errores;

    public ResultadoSintactico() {
        this.errores = new ArrayList<>();
    }

    public ResultadoSintactico(boolean correcto, String mensaje, List<String> errores) {
        this.correcto = correcto;
        this.mensaje = mensaje;
        this.errores = errores;
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

    public List<String> getErrores() {
        return errores;
    }

    public void setErrores(List<String> errores) {
        this.errores = errores;
    }
}
