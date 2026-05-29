/** Chimaltenango 30 de mayo 2026
Proyecto Final Compiladores
Integrantes: 
1990-23-4406	Christopher Obryan Mazariegos Crúz
1990-23-17188	Luis Miguel Vaquiax Camey
1990-23-10442	Keyner Alejandro Rivera Axpuac
1990-23-22934	Freyder José Sequén Urlao
*/
package com.company.api_compilador.grafica;

/**
 * Modelo de respuesta del módulo de gráficas.
 *
 * Indica si el análisis fue correcto, si se encontró una instrucción GRAFICAR
 * y los datos necesarios para construir la URL/local visualización de la gráfica.
 */
public class ResultadoGrafica {

    private boolean correcta;
    private String mensaje;
    private boolean graficaEncontrada;
    private String funcion;
    private String variable;
    private String expresion;
    private String url;

    public ResultadoGrafica() {
    }

    public ResultadoGrafica(boolean correcta, String mensaje, boolean graficaEncontrada,
                            String funcion, String variable, String expresion, String url) {
        this.correcta = correcta;
        this.mensaje = mensaje;
        this.graficaEncontrada = graficaEncontrada;
        this.funcion = funcion;
        this.variable = variable;
        this.expresion = expresion;
        this.url = url;
    }

    public boolean isCorrecta() {
        return correcta;
    }

    public void setCorrecta(boolean correcta) {
        this.correcta = correcta;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public boolean isGraficaEncontrada() {
        return graficaEncontrada;
    }

    public void setGraficaEncontrada(boolean graficaEncontrada) {
        this.graficaEncontrada = graficaEncontrada;
    }

    public String getFuncion() {
        return funcion;
    }

    public void setFuncion(String funcion) {
        this.funcion = funcion;
    }

    public String getVariable() {
        return variable;
    }

    public void setVariable(String variable) {
        this.variable = variable;
    }

    public String getExpresion() {
        return expresion;
    }

    public void setExpresion(String expresion) {
        this.expresion = expresion;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
