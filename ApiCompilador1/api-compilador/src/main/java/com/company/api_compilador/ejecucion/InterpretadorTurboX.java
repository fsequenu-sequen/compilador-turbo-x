package com.company.api_compilador.ejecucion;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InterpretadorTurboX {

    private static final int LIMITE_BUCLE = 10000;

    private final Map<String, ValorRuntime> memoria = new LinkedHashMap<>();
    private final List<String> salida = new ArrayList<>();
    private final List<String> errores = new ArrayList<>();
    private Queue<String> entradas = new ArrayDeque<>();
    private int instruccionesEjecutadas = 0;

    private static final Pattern PATRON_DECLARACION = Pattern.compile(
            "^(ENTERO|REAL|CADENA|CARACTER|LOGICO)\\s+([a-zA-Z_][a-zA-Z0-9_]*)(\\s*=\\s*(.+))?\\s*;?$"
    );

    private static final Pattern PATRON_ASIGNACION = Pattern.compile(
            "^([a-zA-Z_][a-zA-Z0-9_]*)\\s*=\\s*(.+)\\s*;?$"
    );

    public ResultadoEjecucion ejecutar(String codigoFuente, String entradasTexto) {
        memoria.clear();
        salida.clear();
        errores.clear();
        instruccionesEjecutadas = 0;
        entradas = prepararEntradas(entradasTexto);

        if (codigoFuente == null || codigoFuente.trim().isEmpty()) {
            errores.add("No hay código fuente para ejecutar.");
            return construirResultado();
        }

        try {
            List<LineaCodigo> lineas = prepararLineas(codigoFuente);
            int inicio = buscarLineaExacta(lineas, "INICIO", 0);
            int fin = buscarFinPrograma(lineas, inicio + 1);

            if (inicio < 0 || fin < 0 || fin <= inicio) {
                errores.add("No se encontró una estructura válida PROGRAMA / INICIO / FIN.");
                return construirResultado();
            }

            ejecutarBloque(lineas, inicio + 1, fin);

        } catch (EjecucionException ex) {
            errores.add(ex.getMessage());
        } catch (Exception ex) {
            errores.add("Error interno de ejecución: " + ex.getMessage());
        }

        return construirResultado();
    }

    private ResultadoEjecucion construirResultado() {
        boolean correcto = errores.isEmpty();
        String mensaje = correcto
                ? "Ejecución finalizada correctamente."
                : "La ejecución finalizó con " + errores.size() + " error(es).";

        return new ResultadoEjecucion(correcto, mensaje, salida, errores, memoria, instruccionesEjecutadas);
    }

    private Queue<String> prepararEntradas(String entradasTexto) {
        Queue<String> cola = new ArrayDeque<>();

        if (entradasTexto == null || entradasTexto.isEmpty()) {
            return cola;
        }

        String[] valores = entradasTexto.split("\\R", -1);

        for (String valor : valores) {
            cola.add(valor);
        }

        return cola;
    }

    private List<LineaCodigo> prepararLineas(String codigoFuente) {
        String sinComentariosBloque = codigoFuente.replaceAll("(?s)/\\*.*?\\*/", "");
        String[] lineasOriginales = sinComentariosBloque.split("\\R", -1);
        List<LineaCodigo> resultado = new ArrayList<>();

        for (int i = 0; i < lineasOriginales.length; i++) {
            String sinComentario = quitarComentarioLinea(lineasOriginales[i]);
            resultado.addAll(expandirLlaves(sinComentario, i + 1));
        }

        return resultado;
    }

    private List<LineaCodigo> expandirLlaves(String linea, int numeroLinea) {
        List<LineaCodigo> resultado = new ArrayList<>();
        StringBuilder actual = new StringBuilder();
        boolean dentroCadena = false;
        boolean dentroCaracter = false;
        boolean escapado = false;

        for (int i = 0; i < linea.length(); i++) {
            char c = linea.charAt(i);

            if (c == '"' && !dentroCaracter && !escapado) {
                dentroCadena = !dentroCadena;
            } else if (c == '\'' && !dentroCadena && !escapado) {
                dentroCaracter = !dentroCaracter;
            }

            if (!dentroCadena && !dentroCaracter && (c == '{' || c == '}')) {
                agregarLineaSiTieneContenido(resultado, actual.toString(), numeroLinea);
                actual.setLength(0);
                resultado.add(new LineaCodigo(numeroLinea, String.valueOf(c)));
                continue;
            }

            actual.append(c);
            escapado = c == '\\' && !escapado;

            if (c != '\\') {
                escapado = false;
            }
        }

        agregarLineaSiTieneContenido(resultado, actual.toString(), numeroLinea);
        return resultado;
    }

    private void agregarLineaSiTieneContenido(List<LineaCodigo> lineas, String texto, int numeroLinea) {
        String limpia = texto.trim();

        if (!limpia.isEmpty()) {
            lineas.add(new LineaCodigo(numeroLinea, limpia));
        }
    }

    private String quitarComentarioLinea(String linea) {
        boolean dentroCadena = false;
        boolean dentroCaracter = false;

        for (int i = 0; i < linea.length() - 1; i++) {
            char actual = linea.charAt(i);

            if (actual == '"' && !dentroCaracter) {
                dentroCadena = !dentroCadena;
            } else if (actual == '\'' && !dentroCadena) {
                dentroCaracter = !dentroCaracter;
            }

            if (!dentroCadena && !dentroCaracter && actual == '/' && linea.charAt(i + 1) == '/') {
                return linea.substring(0, i);
            }
        }

        return linea;
    }

    private int buscarLineaExacta(List<LineaCodigo> lineas, String texto, int desde) {
        for (int i = Math.max(0, desde); i < lineas.size(); i++) {
            if (lineas.get(i).texto.equalsIgnoreCase(texto)) {
                return i;
            }
        }

        return -1;
    }

    private int buscarFinPrograma(List<LineaCodigo> lineas, int desde) {
        for (int i = lineas.size() - 1; i >= Math.max(0, desde); i--) {
            if (lineas.get(i).texto.equalsIgnoreCase("FIN")) {
                return i;
            }
        }

        return -1;
    }

    private void ejecutarBloque(List<LineaCodigo> lineas, int inicio, int finExclusivo) {
        int i = inicio;

        while (i < finExclusivo && errores.isEmpty()) {
            LineaCodigo linea = lineas.get(i);
            String texto = linea.texto.trim();

            if (texto.isEmpty()
                    || texto.equals("{")
                    || texto.equals("}")
                    || texto.equalsIgnoreCase("INICIO")
                    || texto.equalsIgnoreCase("FIN")
                    || texto.equalsIgnoreCase("SINO")
                    || texto.equalsIgnoreCase("OTRO:")
                    || texto.toUpperCase(Locale.ROOT).startsWith("CASO ")
                    || texto.equalsIgnoreCase("PARAR")
                    || texto.equalsIgnoreCase("PARAR;")
                    || texto.toUpperCase(Locale.ROOT).startsWith("PROGRAMA ")) {
                i++;
                continue;
            }

            String upper = texto.toUpperCase(Locale.ROOT);

            if (upper.startsWith("SI ") || upper.startsWith("SI(")) {
                i = ejecutarSi(lineas, i, finExclusivo);
                continue;
            }

            if (upper.startsWith("MIENTRAS ") || upper.startsWith("MIENTRAS(")) {
                i = ejecutarMientras(lineas, i, finExclusivo);
                continue;
            }

            if (upper.startsWith("EVALUAR")) {
                i = ejecutarEvaluar(lineas, i, finExclusivo);
                continue;
            }

            ejecutarInstruccionSimple(linea);
            i++;
        }
    }

    private void ejecutarInstruccionSimple(LineaCodigo linea) {
        String texto = linea.texto.trim();
        String upper = texto.toUpperCase(Locale.ROOT);

        if (upper.startsWith("GRAFICAR")) {
            instruccionesEjecutadas++;
            return;
        }

        Matcher declaracion = PATRON_DECLARACION.matcher(texto);
        if (declaracion.matches()) {
            ejecutarDeclaracion(declaracion, linea.numero);
            instruccionesEjecutadas++;
            return;
        }

        if (upper.startsWith("IMPRIMIR")) {
            ejecutarImprimir(texto, linea.numero);
            instruccionesEjecutadas++;
            return;
        }

        if (upper.startsWith("LEER")) {
            ejecutarLeer(texto, linea.numero);
            instruccionesEjecutadas++;
            return;
        }

        Matcher asignacion = PATRON_ASIGNACION.matcher(texto);
        if (asignacion.matches()) {
            ejecutarAsignacion(asignacion, linea.numero);
            instruccionesEjecutadas++;
            return;
        }

        throw error(linea.numero, "Instrucción no reconocida durante la ejecución: " + texto);
    }

    private void ejecutarDeclaracion(Matcher declaracion, int numeroLinea) {
        String tipo = declaracion.group(1).toUpperCase(Locale.ROOT);
        String nombre = declaracion.group(2);
        String expresion = declaracion.group(4);

        if (memoria.containsKey(nombre)) {
            throw error(numeroLinea, "La variable '" + nombre + "' ya existe en memoria.");
        }

        ValorRuntime valor = valorPorDefecto(tipo);

        if (expresion != null && !expresion.trim().isEmpty()) {
            valor = convertirA(tipo, evaluarExpresion(limpiarFinSentencia(expresion), numeroLinea), numeroLinea);
        }

        memoria.put(nombre, valor);
    }

    private void ejecutarAsignacion(Matcher asignacion, int numeroLinea) {
        String nombre = asignacion.group(1);
        String expresion = limpiarFinSentencia(asignacion.group(2));

        if (!memoria.containsKey(nombre)) {
            throw error(numeroLinea, "La variable '" + nombre + "' no existe en memoria.");
        }

        ValorRuntime actual = memoria.get(nombre);
        ValorRuntime nuevo = convertirA(actual.getTipo(), evaluarExpresion(expresion, numeroLinea), numeroLinea);
        memoria.put(nombre, nuevo);
    }

    private void ejecutarImprimir(String linea, int numeroLinea) {
        String contenido = extraerContenidoFuncion(linea, "IMPRIMIR", numeroLinea);

        if (contenido.trim().isEmpty()) {
            throw error(numeroLinea, "IMPRIMIR necesita una expresión.");
        }

        ValorRuntime valor = evaluarExpresion(contenido, numeroLinea);
        salida.add(valor.comoTextoSalida());
    }

    private void ejecutarLeer(String linea, int numeroLinea) {
        String variable = extraerContenidoFuncion(linea, "LEER", numeroLinea).trim();

        if (variable.endsWith(";")) {
            variable = variable.substring(0, variable.length() - 1).trim();
        }

        if (!memoria.containsKey(variable)) {
            throw error(numeroLinea, "LEER intenta usar la variable '" + variable + "', pero no existe en memoria.");
        }

        if (entradas.isEmpty()) {
            throw error(numeroLinea, "LEER(" + variable + ") necesita una entrada, pero ya no hay valores disponibles.");
        }

        String entrada = entradas.poll();
        ValorRuntime actual = memoria.get(variable);
        memoria.put(variable, convertirEntrada(actual.getTipo(), entrada, numeroLinea, variable));
    }

    private int ejecutarSi(List<LineaCodigo> lineas, int indiceSi, int finExclusivo) {
        LineaCodigo lineaSi = lineas.get(indiceSi);
        String condicion = extraerEntreParentesis(lineaSi.texto, lineaSi.numero);
        boolean valorCondicion = evaluarExpresion(condicion, lineaSi.numero).comoBooleano();

        int aperturaThen = buscarAperturaBloque(lineas, indiceSi + 1, finExclusivo, lineaSi.numero);
        int cierreThen = buscarCierreBloque(lineas, aperturaThen, finExclusivo);

        int siguiente = cierreThen + 1;
        boolean tieneSino = siguiente < finExclusivo
                && lineas.get(siguiente).texto.equalsIgnoreCase("SINO");

        if (valorCondicion) {
            ejecutarBloque(lineas, aperturaThen + 1, cierreThen);

            // Aunque el SI sea verdadero, si existe SINO debemos saltar todo su bloque.
            if (tieneSino) {
                int aperturaSino = buscarAperturaBloque(lineas, siguiente + 1, finExclusivo, lineas.get(siguiente).numero);
                int cierreSino = buscarCierreBloque(lineas, aperturaSino, finExclusivo);
                siguiente = cierreSino + 1;
            }
        } else if (tieneSino) {
            int aperturaSino = buscarAperturaBloque(lineas, siguiente + 1, finExclusivo, lineas.get(siguiente).numero);
            int cierreSino = buscarCierreBloque(lineas, aperturaSino, finExclusivo);
            ejecutarBloque(lineas, aperturaSino + 1, cierreSino);
            siguiente = cierreSino + 1;
        }

        instruccionesEjecutadas++;
        return siguiente;
    }

    private int ejecutarMientras(List<LineaCodigo> lineas, int indiceMientras, int finExclusivo) {
        LineaCodigo lineaMientras = lineas.get(indiceMientras);
        String condicion = extraerEntreParentesis(lineaMientras.texto, lineaMientras.numero);

        int apertura = buscarAperturaBloque(lineas, indiceMientras + 1, finExclusivo, lineaMientras.numero);
        int cierre = buscarCierreBloque(lineas, apertura, finExclusivo);

        int contador = 0;

        while (evaluarExpresion(condicion, lineaMientras.numero).comoBooleano()) {
            if (contador >= LIMITE_BUCLE) {
                throw error(lineaMientras.numero,
                        "Se detuvo MIENTRAS porque superó el límite de " + LIMITE_BUCLE + " iteraciones.");
            }

            ejecutarBloque(lineas, apertura + 1, cierre);
            contador++;
        }

        instruccionesEjecutadas++;
        return cierre + 1;
    }

    private int ejecutarEvaluar(List<LineaCodigo> lineas, int indiceEvaluar, int finExclusivo) {
        LineaCodigo lineaEvaluar = lineas.get(indiceEvaluar);
        ValorRuntime valorEvaluado = evaluarExpresion(extraerEntreParentesis(lineaEvaluar.texto, lineaEvaluar.numero), lineaEvaluar.numero);

        int apertura = buscarAperturaBloque(lineas, indiceEvaluar + 1, finExclusivo, lineaEvaluar.numero);
        int cierre = buscarCierreBloque(lineas, apertura, finExclusivo);

        int inicioSeleccionado = -1;
        int finSeleccionado = -1;
        int inicioOtro = -1;
        int finOtro = -1;

        int i = apertura + 1;

        while (i < cierre) {
            String texto = lineas.get(i).texto.trim();
            String upper = texto.toUpperCase(Locale.ROOT);

            if (upper.startsWith("CASO ")) {
                int inicioCaso = i + 1;
                int finCaso = buscarFinCaso(lineas, inicioCaso, cierre);
                String expresionCaso = texto.substring(4).replace(":", "").trim();
                ValorRuntime valorCaso = evaluarExpresion(expresionCaso, lineas.get(i).numero);

                if (compararIgual(valorEvaluado, valorCaso)) {
                    inicioSeleccionado = inicioCaso;
                    finSeleccionado = finCaso;
                    break;
                }

                i = finCaso + 1;
                continue;
            }

            if (upper.startsWith("OTRO")) {
                inicioOtro = i + 1;
                finOtro = buscarFinCaso(lineas, inicioOtro, cierre);
                i = finOtro + 1;
                continue;
            }

            i++;
        }

        if (inicioSeleccionado >= 0) {
            ejecutarBloque(lineas, inicioSeleccionado, finSeleccionado);
        } else if (inicioOtro >= 0) {
            ejecutarBloque(lineas, inicioOtro, finOtro);
        }

        instruccionesEjecutadas++;
        return cierre + 1;
    }

    private int buscarFinCaso(List<LineaCodigo> lineas, int inicio, int cierreEvaluar) {
        int i = inicio;

        while (i < cierreEvaluar) {
            String upper = lineas.get(i).texto.toUpperCase(Locale.ROOT);

            if (upper.startsWith("CASO ") || upper.startsWith("OTRO")) {
                return i;
            }

            if (upper.equals("PARAR") || upper.equals("PARAR;")) {
                return i;
            }

            i++;
        }

        return cierreEvaluar;
    }

    private int buscarAperturaBloque(List<LineaCodigo> lineas, int desde, int finExclusivo, int numeroLineaControl) {
        for (int i = desde; i < finExclusivo; i++) {
            if (lineas.get(i).texto.equals("{")) {
                return i;
            }

            if (!lineas.get(i).texto.trim().isEmpty()) {
                break;
            }
        }

        throw error(numeroLineaControl, "Se esperaba '{' para iniciar el bloque.");
    }

    private int buscarCierreBloque(List<LineaCodigo> lineas, int apertura, int finExclusivo) {
        int nivel = 0;

        for (int i = apertura; i < finExclusivo; i++) {
            String texto = lineas.get(i).texto;

            if (texto.equals("{")) {
                nivel++;
            } else if (texto.equals("}")) {
                nivel--;

                if (nivel == 0) {
                    return i;
                }
            }
        }

        throw error(lineas.get(apertura).numero, "No se encontró '}' para cerrar el bloque.");
    }

    private ValorRuntime evaluarExpresion(String expresion, int numeroLinea) {
        try {
            return new ParserExpresiones(expresion, numeroLinea).parsear();
        } catch (EjecucionException ex) {
            throw ex;
        } catch (Exception ex) {
            throw error(numeroLinea, "No se pudo evaluar la expresión '" + expresion + "': " + ex.getMessage());
        }
    }

    private ValorRuntime valorPorDefecto(String tipo) {
        switch (tipo) {
            case "ENTERO":
                return ValorRuntime.entero(0);
            case "REAL":
                return ValorRuntime.real(0.0);
            case "CADENA":
                return ValorRuntime.cadena("");
            case "CARACTER":
                return ValorRuntime.caracter("");
            case "LOGICO":
                return ValorRuntime.logico(false);
            default:
                return ValorRuntime.cadena("");
        }
    }

    private ValorRuntime convertirA(String tipoDestino, ValorRuntime valor, int numeroLinea) {
        if (tipoDestino.equals(valor.getTipo())) {
            return valor;
        }

        if ("REAL".equals(tipoDestino) && "ENTERO".equals(valor.getTipo())) {
            return ValorRuntime.real(valor.comoDouble());
        }

        throw error(numeroLinea,
                "No se puede convertir " + valor.getTipo() + " a " + tipoDestino + " durante la ejecución.");
    }

    private ValorRuntime convertirEntrada(String tipoDestino, String entrada, int numeroLinea, String variable) {
        String limpia = entrada == null ? "" : entrada.trim();

        try {
            switch (tipoDestino) {
                case "ENTERO":
                    return ValorRuntime.entero(Integer.parseInt(limpia));
                case "REAL":
                    return ValorRuntime.real(Double.parseDouble(limpia.replace(",", ".")));
                case "CADENA":
                    return ValorRuntime.cadena(entrada == null ? "" : entrada);
                case "CARACTER":
                    if (limpia.length() != 1) {
                        throw new NumberFormatException("CARACTER necesita exactamente un carácter.");
                    }
                    return ValorRuntime.caracter(limpia);
                case "LOGICO":
                    if (limpia.equalsIgnoreCase("VERDADERO") || limpia.equalsIgnoreCase("true")) {
                        return ValorRuntime.logico(true);
                    }
                    if (limpia.equalsIgnoreCase("FALSO") || limpia.equalsIgnoreCase("false")) {
                        return ValorRuntime.logico(false);
                    }
                    throw new NumberFormatException("LOGICO acepta VERDADERO o FALSO.");
                default:
                    return ValorRuntime.cadena(limpia);
            }
        } catch (NumberFormatException ex) {
            throw error(numeroLinea,
                    "La entrada '" + entrada + "' no es válida para la variable '" + variable
                            + "' de tipo " + tipoDestino + ".");
        }
    }

    private String limpiarFinSentencia(String expresion) {
        String limpia = expresion == null ? "" : expresion.trim();

        while (limpia.endsWith(";") || limpia.endsWith(":")) {
            limpia = limpia.substring(0, limpia.length() - 1).trim();
        }

        return limpia;
    }

    private String extraerContenidoFuncion(String linea, String funcion, int numeroLinea) {
        int inicio = linea.indexOf("(");
        int fin = linea.lastIndexOf(")");

        if (inicio < 0 || fin < 0 || fin <= inicio) {
            throw error(numeroLinea, funcion + " necesita paréntesis válidos.");
        }

        return linea.substring(inicio + 1, fin).trim();
    }

    private String extraerEntreParentesis(String linea, int numeroLinea) {
        int inicio = linea.indexOf("(");
        int fin = linea.lastIndexOf(")");

        if (inicio < 0 || fin < 0 || fin <= inicio) {
            throw error(numeroLinea, "Se esperaba una expresión entre paréntesis.");
        }

        return linea.substring(inicio + 1, fin).trim();
    }

    private boolean compararIgual(ValorRuntime a, ValorRuntime b) {
        if (a.esNumerico() && b.esNumerico()) {
            return Math.abs(a.comoDouble() - b.comoDouble()) < 0.000000001;
        }

        if ("LOGICO".equals(a.getTipo()) && "LOGICO".equals(b.getTipo())) {
            return a.comoBooleano() == b.comoBooleano();
        }

        return a.comoTextoSalida().equals(b.comoTextoSalida());
    }

    private EjecucionException error(int linea, String mensaje) {
        return new EjecucionException("Línea " + linea + ": " + mensaje);
    }

    private static class LineaCodigo {
        private final int numero;
        private final String texto;

        private LineaCodigo(int numero, String texto) {
            this.numero = numero;
            this.texto = texto;
        }
    }

    private static class EjecucionException extends RuntimeException {
        private EjecucionException(String mensaje) {
            super(mensaje);
        }
    }

    private enum TipoToken {
        NUMERO, CADENA, CARACTER, IDENTIFICADOR, OPERADOR, PARENTESIS_ABRE, PARENTESIS_CIERRA, COMA, FIN
    }

    private static class Token {
        private final TipoToken tipo;
        private final String texto;

        private Token(TipoToken tipo, String texto) {
            this.tipo = tipo;
            this.texto = texto;
        }
    }

    private class ParserExpresiones {

        private final List<Token> tokens;
        private final int numeroLinea;
        private int posicion = 0;

        private ParserExpresiones(String expresion, int numeroLinea) {
            this.tokens = tokenizar(expresion == null ? "" : expresion);
            this.numeroLinea = numeroLinea;
        }

        private ValorRuntime parsear() {
            ValorRuntime valor = parseOr();

            if (!actual().tipo.equals(TipoToken.FIN)) {
                throw error(numeroLinea, "Token inesperado en expresión: " + actual().texto);
            }

            return valor;
        }

        private ValorRuntime parseOr() {
            ValorRuntime izquierda = parseAnd();

            while (coincideIdentificador("O") || coincideOperador("||")) {
                ValorRuntime derecha = parseAnd();
                izquierda = ValorRuntime.logico(izquierda.comoBooleano() || derecha.comoBooleano());
            }

            return izquierda;
        }

        private ValorRuntime parseAnd() {
            ValorRuntime izquierda = parseEquality();

            while (coincideIdentificador("Y") || coincideOperador("&&")) {
                ValorRuntime derecha = parseEquality();
                izquierda = ValorRuntime.logico(izquierda.comoBooleano() && derecha.comoBooleano());
            }

            return izquierda;
        }

        private ValorRuntime parseEquality() {
            ValorRuntime izquierda = parseRelational();

            while (true) {
                if (coincideOperador("==")) {
                    ValorRuntime derecha = parseRelational();
                    izquierda = ValorRuntime.logico(compararIgual(izquierda, derecha));
                } else if (coincideOperador("!=")) {
                    ValorRuntime derecha = parseRelational();
                    izquierda = ValorRuntime.logico(!compararIgual(izquierda, derecha));
                } else {
                    return izquierda;
                }
            }
        }

        private ValorRuntime parseRelational() {
            ValorRuntime izquierda = parseAdditive();

            while (true) {
                if (coincideOperador(">")) {
                    ValorRuntime derecha = parseAdditive();
                    izquierda = ValorRuntime.logico(izquierda.comoDouble() > derecha.comoDouble());
                } else if (coincideOperador("<")) {
                    ValorRuntime derecha = parseAdditive();
                    izquierda = ValorRuntime.logico(izquierda.comoDouble() < derecha.comoDouble());
                } else if (coincideOperador(">=")) {
                    ValorRuntime derecha = parseAdditive();
                    izquierda = ValorRuntime.logico(izquierda.comoDouble() >= derecha.comoDouble());
                } else if (coincideOperador("<=")) {
                    ValorRuntime derecha = parseAdditive();
                    izquierda = ValorRuntime.logico(izquierda.comoDouble() <= derecha.comoDouble());
                } else {
                    return izquierda;
                }
            }
        }

        private ValorRuntime parseAdditive() {
            ValorRuntime izquierda = parseMultiplicative();

            while (true) {
                if (coincideOperador("+")) {
                    ValorRuntime derecha = parseMultiplicative();

                    /*
                     * Concatenación formal:
                     * Si cualquiera de los dos lados es texto, + une valores como cadena.
                     * Ejemplo: "su edad es " + edad
                     */
                    if ("CADENA".equals(izquierda.getTipo()) || "CADENA".equals(derecha.getTipo())
                            || "CARACTER".equals(izquierda.getTipo()) || "CARACTER".equals(derecha.getTipo())) {
                        izquierda = ValorRuntime.cadena(izquierda.comoTextoSalida() + derecha.comoTextoSalida());
                    } else if ("REAL".equals(izquierda.getTipo()) || "REAL".equals(derecha.getTipo())) {
                        izquierda = ValorRuntime.real(izquierda.comoDouble() + derecha.comoDouble());
                    } else {
                        izquierda = ValorRuntime.entero(izquierda.comoEntero() + derecha.comoEntero());
                    }
                } else if (coincideOperador("-")) {
                    ValorRuntime derecha = parseMultiplicative();

                    if ("REAL".equals(izquierda.getTipo()) || "REAL".equals(derecha.getTipo())) {
                        izquierda = ValorRuntime.real(izquierda.comoDouble() - derecha.comoDouble());
                    } else {
                        izquierda = ValorRuntime.entero(izquierda.comoEntero() - derecha.comoEntero());
                    }
                } else {
                    return izquierda;
                }
            }
        }

        private ValorRuntime parseMultiplicative() {
            ValorRuntime izquierda = parsePower();

            while (true) {
                if (coincideOperador("*")) {
                    ValorRuntime derecha = parsePower();

                    if ("REAL".equals(izquierda.getTipo()) || "REAL".equals(derecha.getTipo())) {
                        izquierda = ValorRuntime.real(izquierda.comoDouble() * derecha.comoDouble());
                    } else {
                        izquierda = ValorRuntime.entero(izquierda.comoEntero() * derecha.comoEntero());
                    }
                } else if (coincideOperador("/")) {
                    ValorRuntime derecha = parsePower();

                    if (Math.abs(derecha.comoDouble()) < 0.0000000001) {
                        throw error(numeroLinea, "No se puede dividir entre cero durante la ejecución.");
                    }

                    izquierda = ValorRuntime.real(izquierda.comoDouble() / derecha.comoDouble());
                } else if (coincideOperador("%")) {
                    ValorRuntime derecha = parsePower();

                    if (Math.abs(derecha.comoDouble()) < 0.0000000001) {
                        throw error(numeroLinea, "No se puede calcular módulo entre cero durante la ejecución.");
                    }

                    if ("REAL".equals(izquierda.getTipo()) || "REAL".equals(derecha.getTipo())) {
                        izquierda = ValorRuntime.real(izquierda.comoDouble() % derecha.comoDouble());
                    } else {
                        izquierda = ValorRuntime.entero(izquierda.comoEntero() % derecha.comoEntero());
                    }
                } else {
                    return izquierda;
                }
            }
        }

        private ValorRuntime parsePower() {
            ValorRuntime izquierda = parseUnary();

            if (coincideOperador("^")) {
                ValorRuntime derecha = parsePower();
                izquierda = ValorRuntime.real(Math.pow(izquierda.comoDouble(), derecha.comoDouble()));
            }

            return izquierda;
        }

        private ValorRuntime parseUnary() {
            if (coincideOperador("-")) {
                ValorRuntime valor = parseUnary();

                if ("REAL".equals(valor.getTipo())) {
                    return ValorRuntime.real(-valor.comoDouble());
                }

                return ValorRuntime.entero(-valor.comoEntero());
            }

            if (coincideOperador("+")) {
                return parseUnary();
            }

            if (coincideOperador("!") || coincideIdentificador("NO")) {
                ValorRuntime valor = parseUnary();
                return ValorRuntime.logico(!valor.comoBooleano());
            }

            return parsePrimary();
        }

        private ValorRuntime parsePrimary() {
            Token token = actual();

            if (coincide(TipoToken.NUMERO)) {
                if (token.texto.contains(".")) {
                    return ValorRuntime.real(Double.parseDouble(token.texto));
                }

                return ValorRuntime.entero(Integer.parseInt(token.texto));
            }

            if (coincide(TipoToken.CADENA)) {
                return ValorRuntime.cadena(token.texto);
            }

            if (coincide(TipoToken.CARACTER)) {
                return ValorRuntime.caracter(token.texto);
            }

            if (coincide(TipoToken.PARENTESIS_ABRE)) {
                ValorRuntime valor = parseOr();
                consumir(TipoToken.PARENTESIS_CIERRA, "Se esperaba ')' en la expresión.");
                return valor;
            }

            if (token.tipo.equals(TipoToken.IDENTIFICADOR)) {
                avanzar();
                String id = token.texto;

                if (id.equalsIgnoreCase("VERDADERO")) {
                    return ValorRuntime.logico(true);
                }

                if (id.equalsIgnoreCase("FALSO")) {
                    return ValorRuntime.logico(false);
                }

                if (coincide(TipoToken.PARENTESIS_ABRE)) {
                    ValorRuntime argumento = parseOr();
                    consumir(TipoToken.PARENTESIS_CIERRA, "Se esperaba ')' al cerrar función matemática.");
                    return ejecutarFuncionMatematica(id, argumento);
                }

                if (!memoria.containsKey(id)) {
                    throw error(numeroLinea, "La variable '" + id + "' no existe en memoria.");
                }

                return memoria.get(id);
            }

            throw error(numeroLinea, "Expresión inválida. Token recibido: " + token.texto);
        }

        private ValorRuntime ejecutarFuncionMatematica(String nombre, ValorRuntime argumento) {
            double valor = argumento.comoDouble();
            String n = nombre.toLowerCase(Locale.ROOT);

            switch (n) {
                case "sin":
                    return ValorRuntime.real(Math.sin(valor));
                case "cos":
                    return ValorRuntime.real(Math.cos(valor));
                case "tan":
                    return ValorRuntime.real(Math.tan(valor));
                case "sqrt":
                    return ValorRuntime.real(Math.sqrt(valor));
                case "abs":
                    return ValorRuntime.real(Math.abs(valor));
                case "log":
                    return ValorRuntime.real(Math.log10(valor));
                case "ln":
                    return ValorRuntime.real(Math.log(valor));
                case "exp":
                    return ValorRuntime.real(Math.exp(valor));
                default:
                    throw error(numeroLinea, "Función matemática no reconocida: " + nombre);
            }
        }

        private List<Token> tokenizar(String expresion) {
            List<Token> lista = new ArrayList<>();
            int i = 0;

            while (i < expresion.length()) {
                char c = expresion.charAt(i);

                if (Character.isWhitespace(c)) {
                    i++;
                    continue;
                }

                if (Character.isDigit(c)) {
                    int inicio = i;
                    boolean punto = false;

                    while (i < expresion.length()) {
                        char actual = expresion.charAt(i);

                        if (Character.isDigit(actual)) {
                            i++;
                        } else if (actual == '.' && !punto) {
                            punto = true;
                            i++;
                        } else {
                            break;
                        }
                    }

                    lista.add(new Token(TipoToken.NUMERO, expresion.substring(inicio, i)));
                    continue;
                }

                if (c == '"') {
                    StringBuilder sb = new StringBuilder();
                    i++;

                    while (i < expresion.length() && expresion.charAt(i) != '"') {
                        sb.append(expresion.charAt(i));
                        i++;
                    }

                    if (i >= expresion.length()) {
                        throw error(numeroLinea, "Cadena sin cerrar.");
                    }

                    i++;
                    lista.add(new Token(TipoToken.CADENA, sb.toString()));
                    continue;
                }

                if (c == '\'') {
                    StringBuilder sb = new StringBuilder();
                    i++;

                    while (i < expresion.length() && expresion.charAt(i) != '\'') {
                        sb.append(expresion.charAt(i));
                        i++;
                    }

                    if (i >= expresion.length()) {
                        throw error(numeroLinea, "Carácter sin cerrar.");
                    }

                    i++;
                    lista.add(new Token(TipoToken.CARACTER, sb.toString()));
                    continue;
                }

                if (Character.isLetter(c) || c == '_') {
                    int inicio = i;

                    while (i < expresion.length()
                            && (Character.isLetterOrDigit(expresion.charAt(i)) || expresion.charAt(i) == '_')) {
                        i++;
                    }

                    lista.add(new Token(TipoToken.IDENTIFICADOR, expresion.substring(inicio, i)));
                    continue;
                }

                if (c == '(') {
                    lista.add(new Token(TipoToken.PARENTESIS_ABRE, "("));
                    i++;
                    continue;
                }

                if (c == ')') {
                    lista.add(new Token(TipoToken.PARENTESIS_CIERRA, ")"));
                    i++;
                    continue;
                }

                if (c == ',') {
                    lista.add(new Token(TipoToken.COMA, ","));
                    i++;
                    continue;
                }

                String dos = i + 1 < expresion.length() ? expresion.substring(i, i + 2) : "";

                if (Arrays.asList(">=", "<=", "==", "!=", "&&", "||").contains(dos)) {
                    lista.add(new Token(TipoToken.OPERADOR, dos));
                    i += 2;
                    continue;
                }

                if ("+-*/%^<>!".indexOf(c) >= 0) {
                    lista.add(new Token(TipoToken.OPERADOR, String.valueOf(c)));
                    i++;
                    continue;
                }

                throw error(numeroLinea, "Carácter no válido en expresión: " + c);
            }

            lista.add(new Token(TipoToken.FIN, ""));
            return lista;
        }

        private Token actual() {
            return tokens.get(posicion);
        }

        private Token avanzar() {
            if (!actual().tipo.equals(TipoToken.FIN)) {
                posicion++;
            }

            return tokens.get(posicion - 1);
        }

        private boolean coincide(TipoToken tipo) {
            if (actual().tipo.equals(tipo)) {
                avanzar();
                return true;
            }

            return false;
        }

        private boolean coincideOperador(String operador) {
            if (actual().tipo.equals(TipoToken.OPERADOR) && actual().texto.equals(operador)) {
                avanzar();
                return true;
            }

            return false;
        }

        private boolean coincideIdentificador(String identificador) {
            if (actual().tipo.equals(TipoToken.IDENTIFICADOR)
                    && actual().texto.equalsIgnoreCase(identificador)) {
                avanzar();
                return true;
            }

            return false;
        }

        private void consumir(TipoToken tipo, String mensaje) {
            if (!coincide(tipo)) {
                throw error(numeroLinea, mensaje);
            }
        }
    }
}
