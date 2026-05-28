package com.company.api_compilador;

public class ValorRuntime {

    private String tipo;
    private Object valor;
    private String valorTexto;

    public ValorRuntime() {
    }

    public ValorRuntime(String tipo, Object valor) {
        this.tipo = tipo;
        this.valor = valor;
        this.valorTexto = convertirTexto(tipo, valor);
    }

    public static ValorRuntime entero(int valor) {
        return new ValorRuntime("ENTERO", valor);
    }

    public static ValorRuntime real(double valor) {
        return new ValorRuntime("REAL", valor);
    }

    public static ValorRuntime cadena(String valor) {
        return new ValorRuntime("CADENA", valor);
    }

    public static ValorRuntime caracter(String valor) {
        return new ValorRuntime("CARACTER", valor);
    }

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

    public boolean esNumerico() {
        return "ENTERO".equals(tipo) || "REAL".equals(tipo);
    }

    public double comoDouble() {
        if ("ENTERO".equals(tipo)) {
            return ((Number) valor).doubleValue();
        }

        if ("REAL".equals(tipo)) {
            return ((Number) valor).doubleValue();
        }

        throw new RuntimeException("Se esperaba un valor numérico, pero se obtuvo " + tipo + ".");
    }

    public int comoEntero() {
        if ("ENTERO".equals(tipo)) {
            return ((Number) valor).intValue();
        }

        throw new RuntimeException("Se esperaba un valor ENTERO, pero se obtuvo " + tipo + ".");
    }

    public boolean comoBooleano() {
        if ("LOGICO".equals(tipo)) {
            return (Boolean) valor;
        }

        throw new RuntimeException("Se esperaba un valor LOGICO, pero se obtuvo " + tipo + ".");
    }

    public String comoTextoSalida() {
        return convertirTexto(tipo, valor);
    }

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
