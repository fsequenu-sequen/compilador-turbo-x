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
 * Representa un valor en tiempo de ejecución.
 *
 * Guarda el tipo de dato Turbo X, el valor real usado internamente por Java
 * y una versión en texto para mostrar en la interfaz o en la tabla de variables.
 */
public class ValorRuntime {

    // Tipo lógico usado por Turbo X: ENTERO, REAL, CADENA, CARACTER o LOGICO.
    private String tipo;

    // Valor interno usado por Java para operar durante la ejecución.
    private Object valor;

    // Versión textual del valor, útil para mostrar resultados en pantalla.
    private String valorTexto;

    public ValorRuntime() {
    }

    public ValorRuntime(String tipo, Object valor) {
        this.tipo = tipo;
        this.valor = valor;
        this.valorTexto = convertirTexto(tipo, valor);
    }

    /** Crea un valor runtime de tipo ENTERO. */
    public static ValorRuntime entero(int valor) {
        return new ValorRuntime("ENTERO", valor);
    }

    /** Crea un valor runtime de tipo REAL. */
    public static ValorRuntime real(double valor) {
        return new ValorRuntime("REAL", valor);
    }

    /** Crea un valor runtime de tipo CADENA. */
    public static ValorRuntime cadena(String valor) {
        return new ValorRuntime("CADENA", valor);
    }

    /** Crea un valor runtime de tipo CARACTER. */
    public static ValorRuntime caracter(String valor) {
        return new ValorRuntime("CARACTER", valor);
    }

    /** Crea un valor runtime de tipo LOGICO. */
    public static ValorRuntime logico(boolean valor) {
        return new ValorRuntime("LOGICO", valor);
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
        this.valorTexto = convertirTexto(tipo, valor);
    }

    public Object getValor() {
        return valor;
    }

    public void setValor(Object valor) {
        this.valor = valor;
        this.valorTexto = convertirTexto(tipo, valor);
    }

    public String getValorTexto() {
        return valorTexto;
    }

    public void setValorTexto(String valorTexto) {
        this.valorTexto = valorTexto;
    }

    /** Indica si el valor puede participar en operaciones aritméticas. */
    public boolean esNumerico() {
        return "ENTERO".equals(tipo) || "REAL".equals(tipo);
    }

    /** Convierte ENTERO o REAL a double para cálculos numéricos. */
    public double comoDouble() {
        if ("ENTERO".equals(tipo)) {
            return ((Number) valor).doubleValue();
        }

        if ("REAL".equals(tipo)) {
            return ((Number) valor).doubleValue();
        }

        throw new RuntimeException("Se esperaba un valor numérico, pero se obtuvo " + tipo + ".");
    }

    /** Obtiene el valor como entero cuando el tipo runtime es ENTERO. */
    public int comoEntero() {
        if ("ENTERO".equals(tipo)) {
            return ((Number) valor).intValue();
        }

        throw new RuntimeException("Se esperaba un valor ENTERO, pero se obtuvo " + tipo + ".");
    }

    /** Obtiene el valor booleano cuando el tipo runtime es LOGICO. */
    public boolean comoBooleano() {
        if ("LOGICO".equals(tipo)) {
            return (Boolean) valor;
        }

        throw new RuntimeException("Se esperaba un valor LOGICO, pero se obtuvo " + tipo + ".");
    }

    /** Devuelve el valor con el formato que debe ver el usuario final. */
    public String comoTextoSalida() {
        return convertirTexto(tipo, valor);
    }

    /**
     * Normaliza el valor para mostrarlo en salida.
     *
     * Ejemplos:
     * - LOGICO se muestra como VERDADERO o FALSO.
     * - REAL elimina ceros innecesarios, pero conserva .0 si es entero real.
     */
    private static String convertirTexto(String tipo, Object valor) {
        if (valor == null) {
            return "";
        }

        if ("LOGICO".equals(tipo)) {
            return ((Boolean) valor) ? "VERDADERO" : "FALSO";
        }

        if ("REAL".equals(tipo)) {
            double numero = ((Number) valor).doubleValue();

            if (Double.isNaN(numero) || Double.isInfinite(numero)) {
                return String.valueOf(numero);
            }

            if (Math.abs(numero - Math.rint(numero)) < 0.0000000001) {
                return String.format(java.util.Locale.US, "%.1f", numero);
            }

            String texto = String.format(java.util.Locale.US, "%.10f", numero);
            while (texto.contains(".") && texto.endsWith("0")) {
                texto = texto.substring(0, texto.length() - 1);
            }
            return texto;
        }

        return String.valueOf(valor);
    }
}
