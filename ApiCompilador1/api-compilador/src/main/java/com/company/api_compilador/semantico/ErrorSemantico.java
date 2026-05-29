/** Chimaltenango 30 de mayo 2026
Proyecto Final Compiladores
Integrantes: 
1990-23-4406	Christopher Obryan Mazariegos Crúz
1990-23-17188	Luis Miguel Vaquiax Camey
1990-23-10442	Keyner Alejandro Rivera Axpuac
1990-23-22934	Freyder José Sequén Urlao
*/
package com.company.api_compilador.semantico;

/**
 * Representa un error detectado durante el análisis semántico.
 *
 * Guarda línea, lexema relacionado y descripción clara del problema para
 * mostrarlo en la tabla de errores del frontend.
 */
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
