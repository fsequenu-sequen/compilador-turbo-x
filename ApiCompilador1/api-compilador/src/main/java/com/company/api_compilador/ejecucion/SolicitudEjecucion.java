/** Chimaltenango 30 de mayo 2026
Proyecto Final Compiladores
Integrantes: 
1990-23-4406	Christopher Obryan Mazariegos Crúz
1990-23-17188	Luis Miguel Vaquiax Camey
1990-23-10442	Keyner Alejandro Rivera Axpuac
1990-23-22934	Freyder José Sequén Urlao
*/
package com.company.api_compilador.ejecucion;

/**
 * Modelo de entrada para ejecutar un programa Turbo X.
 *
 * El frontend envía aquí el código fuente y, opcionalmente, valores de entrada
 * para simular las instrucciones LEER.
 */
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
