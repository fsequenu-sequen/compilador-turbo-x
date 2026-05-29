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
 * Representa una entrada de la tabla de símbolos.
 *
 * Cada símbolo describe una variable o elemento reconocido: nombre, tipo,
 * categoría, línea de declaración y si ya fue inicializado.
 */
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

