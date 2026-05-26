package com.company.api_compilador;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnalizadorSemantico {

    private final TablaSimbolos tabla = new TablaSimbolos();
    private final List<ErrorSemantico> errores = new ArrayList<>();

    private static final Set<String> TIPOS = new HashSet<>(Arrays.asList(
            "ENTERO", "REAL", "CADENA", "CARACTER", "LOGICO"
    ));

    private static final Set<String> PALABRAS_RESERVADAS = new HashSet<>(Arrays.asList(
            "PROGRAMA", "INICIO", "FIN",
            "ENTERO", "REAL", "CADENA", "CARACTER", "LOGICO",
            "VERDADERO", "FALSO",
            "SI", "ENTONCES", "SINO", "MIENTRAS", "HACER",
            "IMPRIMIR", "LEER",
            "EVALUAR", "CASO", "OTRO", "PARAR",
            "GRAFICAR", "Y", "O", "NO"
    ));

    private static final Pattern PATRON_DECLARACION = Pattern.compile(
            "^(ENTERO|REAL|CADENA|CARACTER|LOGICO)\\s+([a-zA-Z_][a-zA-Z0-9_]*)(\\s*=\\s*(.+))?\\s*;?$"
    );

    private static final Pattern PATRON_ASIGNACION = Pattern.compile(
            "^([a-zA-Z_][a-zA-Z0-9_]*)\\s*=\\s*(.+)\\s*;?$"
    );

    private static final Pattern PATRON_IDENTIFICADOR = Pattern.compile(
            "\\b[a-zA-Z_][a-zA-Z0-9_]*\\b"
    );

    public ResultadoSemantico analizar(String codigoFuente) {
        if (codigoFuente == null || codigoFuente.trim().isEmpty()) {
            errores.add(new ErrorSemantico(1, "", "No hay código fuente para analizar."));
            return construirResultado();
        }

        String codigoSinComentarios = eliminarComentariosDeBloque(codigoFuente);
        String[] lineas = codigoSinComentarios.split("\\R");

        for (int i = 0; i < lineas.length; i++) {
            int numeroLinea = i + 1;
            String lineaOriginal = quitarComentarioLinea(lineas[i]);
            String linea = lineaOriginal.trim();

            if (linea.isEmpty()) {
                continue;
            }

            analizarLinea(linea, numeroLinea);
        }

        return construirResultado();
    }

    private void analizarLinea(String linea, int numeroLinea) {
        String lineaSinLlaves = linea.replace("{", "").replace("}", "").trim();

        if (lineaSinLlaves.isEmpty()
                || lineaSinLlaves.equals("INICIO")
                || lineaSinLlaves.equals("FIN")
                || lineaSinLlaves.equals("PARAR;")
                || lineaSinLlaves.equals("PARAR")
                || lineaSinLlaves.equals("SINO")
                || lineaSinLlaves.equals("OTRO:")
                || lineaSinLlaves.startsWith("PROGRAMA ")) {
            return;
        }

        Matcher declaracion = PATRON_DECLARACION.matcher(lineaSinLlaves);
        if (declaracion.matches()) {
            analizarDeclaracion(declaracion, numeroLinea);
            return;
        }

        if (lineaSinLlaves.startsWith("LEER")) {
            analizarLeer(lineaSinLlaves, numeroLinea);
            return;
        }

        if (lineaSinLlaves.startsWith("IMPRIMIR")) {
            analizarImprimir(lineaSinLlaves, numeroLinea);
            return;
        }

        /*
         * IMPORTANTE:
         * Antes se usaba startsWith("SI"), pero eso provocaba que la palabra SINO
         * fuera interpretada como SI, porque SINO también inicia con "SI".
         * Por eso ahora validamos que realmente sea SI seguido de espacio o paréntesis.
         */
        if (esInicioSi(lineaSinLlaves)) {
            analizarCondicion(lineaSinLlaves, numeroLinea, "SI");
            return;
        }

        if (esInicioMientras(lineaSinLlaves)) {
            analizarCondicion(lineaSinLlaves, numeroLinea, "MIENTRAS");
            return;
        }

        if (lineaSinLlaves.startsWith("EVALUAR")) {
            analizarEvaluar(lineaSinLlaves, numeroLinea);
            return;
        }

        if (lineaSinLlaves.startsWith("CASO")) {
            analizarCaso(lineaSinLlaves, numeroLinea);
            return;
        }

        if (lineaSinLlaves.startsWith("GRAFICAR")) {
            analizarGraficar(lineaSinLlaves, numeroLinea);
            return;
        }

        Matcher asignacion = PATRON_ASIGNACION.matcher(lineaSinLlaves);
        if (asignacion.matches()) {
            analizarAsignacion(asignacion, numeroLinea);
        }
    }

    private boolean esInicioSi(String linea) {
        return linea.equals("SI")
                || linea.startsWith("SI ")
                || linea.startsWith("SI(");
    }

    private boolean esInicioMientras(String linea) {
        return linea.equals("MIENTRAS")
                || linea.startsWith("MIENTRAS ")
                || linea.startsWith("MIENTRAS(");
    }

    private void analizarDeclaracion(Matcher declaracion, int numeroLinea) {
        String tipo = declaracion.group(1);
        String nombre = declaracion.group(2);
        String expresionInicial = declaracion.group(4);

        if (PALABRAS_RESERVADAS.contains(nombre.toUpperCase())) {
            agregarError(numeroLinea, nombre, "No se puede usar una palabra reservada como nombre de variable.");
            return;
        }

        boolean tieneValorInicial = expresionInicial != null && !expresionInicial.trim().isEmpty();

        Simbolo simbolo = new Simbolo(nombre, tipo, "VARIABLE", numeroLinea, tieneValorInicial);
        boolean declarada = tabla.declarar(simbolo);

        if (!declarada) {
            agregarError(numeroLinea, nombre, "La variable '" + nombre + "' ya fue declarada anteriormente.");
            return;
        }

        if (tieneValorInicial) {
            String expresion = limpiarExpresion(expresionInicial);
            String tipoExpresion = inferirTipo(expresion, numeroLinea, "declaracion");

            if (!esCompatible(tipo, tipoExpresion)) {
                agregarError(numeroLinea, expresion,
                        "No se puede asignar una expresión de tipo " + tipoExpresion
                                + " a la variable '" + nombre + "' de tipo " + tipo + ".");
            }
        }
    }

    private void analizarAsignacion(Matcher asignacion, int numeroLinea) {
        String nombre = asignacion.group(1);
        String expresion = limpiarExpresion(asignacion.group(2));

        if (!tabla.existe(nombre)) {
            agregarError(numeroLinea, nombre,
                    "La variable '" + nombre + "' se está usando antes de ser declarada.");
            inferirTipo(expresion, numeroLinea, "asignacion");
            return;
        }

        Simbolo simbolo = tabla.obtener(nombre);
        String tipoExpresion = inferirTipo(expresion, numeroLinea, "asignacion");

        if (!esCompatible(simbolo.getTipo(), tipoExpresion)) {
            agregarError(numeroLinea, expresion,
                    "No se puede asignar una expresión de tipo " + tipoExpresion
                            + " a la variable '" + nombre + "' de tipo " + simbolo.getTipo() + ".");
        } else {
            tabla.marcarInicializado(nombre);
        }
    }

    private void analizarLeer(String linea, int numeroLinea) {
        String contenido = extraerContenidoFuncion(linea, "LEER");
        contenido = contenido.replace(";", "").trim();

        if (contenido.isEmpty()) {
            agregarError(numeroLinea, "LEER", "La instrucción LEER necesita una variable.");
            return;
        }

        if (!esIdentificadorValido(contenido)) {
            agregarError(numeroLinea, contenido, "LEER solo puede recibir el nombre de una variable.");
            return;
        }

        if (!tabla.existe(contenido)) {
            agregarError(numeroLinea, contenido,
                    "La variable '" + contenido + "' se está leyendo antes de ser declarada.");
        } else {
            tabla.marcarInicializado(contenido);
        }
    }

    private void analizarImprimir(String linea, int numeroLinea) {
        String contenido = extraerContenidoFuncion(linea, "IMPRIMIR");
        if (contenido.isEmpty()) {
            agregarError(numeroLinea, "IMPRIMIR", "La instrucción IMPRIMIR necesita una expresión.");
            return;
        }

        inferirTipo(contenido, numeroLinea, "imprimir");
    }

    private void analizarCondicion(String linea, int numeroLinea, String instruccion) {
        String condicion = extraerEntreParentesis(linea);

        if (condicion.isEmpty()) {
            agregarError(numeroLinea, instruccion, "La condición de " + instruccion + " está vacía.");
            return;
        }

        String tipoCondicion = inferirTipo(condicion, numeroLinea, "condicion");

        if (!"LOGICO".equals(tipoCondicion)) {
            agregarError(numeroLinea, condicion,
                    "La condición de " + instruccion + " debe ser de tipo LOGICO, pero se obtuvo " + tipoCondicion + ".");
        }
    }

    private void analizarEvaluar(String linea, int numeroLinea) {
        String expresion = extraerEntreParentesis(linea);
        if (expresion.isEmpty()) {
            agregarError(numeroLinea, "EVALUAR", "La instrucción EVALUAR necesita una expresión.");
            return;
        }

        inferirTipo(expresion, numeroLinea, "evaluar");
    }

    private void analizarCaso(String linea, int numeroLinea) {
        String caso = linea.replaceFirst("^CASO", "").replace(":", "").trim();
        if (caso.isEmpty()) {
            agregarError(numeroLinea, "CASO", "La instrucción CASO necesita un valor.");
            return;
        }

        inferirTipo(caso, numeroLinea, "caso");
    }

    private void analizarGraficar(String linea, int numeroLinea) {
        int indiceIgual = linea.indexOf("=");

        if (indiceIgual < 0) {
            agregarError(numeroLinea, "GRAFICAR", "La instrucción GRAFICAR necesita una expresión después de '='.");
            return;
        }

        String expresion = limpiarExpresion(linea.substring(indiceIgual + 1));
        String tipoExpresion = inferirTipoConVariableLocalX(expresion, numeroLinea);

        if (!"ENTERO".equals(tipoExpresion) && !"REAL".equals(tipoExpresion) && !"DESCONOCIDO".equals(tipoExpresion)) {
            agregarError(numeroLinea, expresion,
                    "La función a graficar debe ser numérica, pero se obtuvo tipo " + tipoExpresion + ".");
        }
    }

    private String inferirTipo(String expresion, int numeroLinea, String contexto) {
        expresion = limpiarExpresion(expresion);

        if (expresion.isEmpty()) {
            return "DESCONOCIDO";
        }

        if (esCadena(expresion)) {
            return "CADENA";
        }

        if (esCaracter(expresion)) {
            return "CARACTER";
        }

        if (expresion.equals("VERDADERO") || expresion.equals("FALSO")) {
            return "LOGICO";
        }

        if (esNumeroEntero(expresion)) {
            return "ENTERO";
        }

        if (esNumeroReal(expresion)) {
            return "REAL";
        }

        if (esIdentificadorValido(expresion)) {
            return tipoDeIdentificador(expresion, numeroLinea);
        }

        if (expresion.startsWith("NO ")) {
            String tipo = inferirTipo(expresion.substring(3), numeroLinea, contexto);
            if (!"LOGICO".equals(tipo)) {
                agregarError(numeroLinea, expresion, "El operador NO solo puede aplicarse a expresiones LOGICAS.");
            }
            return "LOGICO";
        }

        if (expresion.startsWith("!")) {
            String tipo = inferirTipo(expresion.substring(1), numeroLinea, contexto);
            if (!"LOGICO".equals(tipo)) {
                agregarError(numeroLinea, expresion, "El operador ! solo puede aplicarse a expresiones LOGICAS.");
            }
            return "LOGICO";
        }

        if (contieneOperadorLogico(expresion)) {
            validarIdentificadores(expresion, numeroLinea);
            validarOperandosLogicos(expresion, numeroLinea);
            return "LOGICO";
        }

        if (contieneOperadorRelacional(expresion)) {
            return inferirTipoRelacional(expresion, numeroLinea);
        }

        if (contieneOperadorAritmetico(expresion)) {
            return inferirTipoAritmetico(expresion, numeroLinea);
        }

        validarIdentificadores(expresion, numeroLinea);
        return "DESCONOCIDO";
    }

    private String inferirTipoConVariableLocalX(String expresion, int numeroLinea) {
        boolean xYaExiste = tabla.existe("x");

        if (!xYaExiste) {
            tabla.declarar(new Simbolo("x", "REAL", "VARIABLE_LOCAL_GRAFICA", numeroLinea, true));
        }

        return inferirTipo(expresion, numeroLinea, "graficar");
    }

    private String inferirTipoRelacional(String expresion, int numeroLinea) {
        String operador = obtenerOperadorRelacional(expresion);

        if (operador == null) {
            return "LOGICO";
        }

        String[] partes = expresion.split(Pattern.quote(operador), 2);

        if (partes.length < 2) {
            agregarError(numeroLinea, expresion, "Expresión relacional incompleta.");
            return "LOGICO";
        }

        String tipoIzquierda = inferirTipo(partes[0], numeroLinea, "relacional");
        String tipoDerecha = inferirTipo(partes[1], numeroLinea, "relacional");

        if (operador.equals(">") || operador.equals("<") || operador.equals(">=") || operador.equals("<=")) {
            if (!esNumerico(tipoIzquierda) || !esNumerico(tipoDerecha)) {
                agregarError(numeroLinea, expresion,
                        "Los operadores " + operador + " solo pueden comparar valores numéricos.");
            }
        }

        if (operador.equals("==") || operador.equals("!=")) {
            boolean compatibles = tipoIzquierda.equals(tipoDerecha)
                    || (esNumerico(tipoIzquierda) && esNumerico(tipoDerecha));

            if (!compatibles) {
                agregarError(numeroLinea, expresion,
                        "No se pueden comparar valores de tipo " + tipoIzquierda + " y " + tipoDerecha + ".");
            }
        }

        return "LOGICO";
    }

    private String inferirTipoAritmetico(String expresion, int numeroLinea) {
        List<String> operandos = obtenerOperandosAritmeticos(expresion);
        boolean tieneReal = false;

        for (String operando : operandos) {
            String limpio = limpiarExpresion(operando);

            if (limpio.isEmpty()) {
                continue;
            }

            String tipo = inferirTipo(limpio, numeroLinea, "aritmetico");

            if (!esNumerico(tipo)) {
                agregarError(numeroLinea, limpio,
                        "La expresión aritmética contiene un valor de tipo " + tipo + ", pero se esperaba ENTERO o REAL.");
            }

            if ("REAL".equals(tipo)) {
                tieneReal = true;
            }
        }

        if (expresion.contains("/")) {
            return "REAL";
        }

        return tieneReal ? "REAL" : "ENTERO";
    }

    private void validarOperandosLogicos(String expresion, int numeroLinea) {
        String[] partes = expresion.split("\\s+(Y|O)\\s+|&&|\\|\\|");

        for (String parte : partes) {
            String limpio = limpiarExpresion(parte);
            if (limpio.isEmpty()) {
                continue;
            }

            String tipo = inferirTipo(limpio, numeroLinea, "logico");
            if (!"LOGICO".equals(tipo)) {
                agregarError(numeroLinea, limpio,
                        "Los operadores lógicos solo pueden trabajar con expresiones LOGICAS.");
            }
        }
    }

    private void validarIdentificadores(String expresion, int numeroLinea) {
        String expresionSinLiterales = quitarLiterales(expresion);
        Matcher matcher = PATRON_IDENTIFICADOR.matcher(expresionSinLiterales);

        while (matcher.find()) {
            String posibleIdentificador = matcher.group();

            if (PALABRAS_RESERVADAS.contains(posibleIdentificador.toUpperCase())) {
                continue;
            }

            if (!tabla.existe(posibleIdentificador)) {
                agregarError(numeroLinea, posibleIdentificador,
                        "La variable '" + posibleIdentificador + "' se está usando antes de ser declarada.");
            }
        }
    }

    private String tipoDeIdentificador(String nombre, int numeroLinea) {
        if (!tabla.existe(nombre)) {
            agregarError(numeroLinea, nombre,
                    "La variable '" + nombre + "' se está usando antes de ser declarada.");
            return "DESCONOCIDO";
        }

        return tabla.obtener(nombre).getTipo();
    }

    private boolean esCompatible(String tipoDestino, String tipoOrigen) {
        if (tipoOrigen == null || tipoOrigen.equals("DESCONOCIDO")) {
            return true;
        }

        if (tipoDestino.equals(tipoOrigen)) {
            return true;
        }

        return tipoDestino.equals("REAL") && tipoOrigen.equals("ENTERO");
    }

    private boolean esNumerico(String tipo) {
        return "ENTERO".equals(tipo) || "REAL".equals(tipo);
    }

    private boolean contieneOperadorAritmetico(String expresion) {
        String sinLiterales = quitarLiterales(expresion);
        return sinLiterales.matches(".*(\\+|\\-|\\*|/|%|\\^).*");
    }

    private boolean contieneOperadorRelacional(String expresion) {
        String sinLiterales = quitarLiterales(expresion);
        return sinLiterales.contains(">=")
                || sinLiterales.contains("<=")
                || sinLiterales.contains("==")
                || sinLiterales.contains("!=")
                || sinLiterales.contains(">")
                || sinLiterales.contains("<");
    }

    private boolean contieneOperadorLogico(String expresion) {
        String sinLiterales = quitarLiterales(expresion);
        return sinLiterales.matches(".*\\s(Y|O)\\s.*")
                || sinLiterales.contains("&&")
                || sinLiterales.contains("||");
    }

    private String obtenerOperadorRelacional(String expresion) {
        String[] operadores = {">=", "<=", "==", "!=", ">", "<"};

        for (String operador : operadores) {
            if (expresion.contains(operador)) {
                return operador;
            }
        }

        return null;
    }

    private List<String> obtenerOperandosAritmeticos(String expresion) {
        String normalizada = expresion.replace("**", "^");
        return Arrays.asList(normalizada.split("\\+|\\-|\\*|/|%|\\^"));
    }

    private String extraerContenidoFuncion(String linea, String funcion) {
        String contenido = linea.replaceFirst("^" + funcion, "").trim();

        if (contenido.startsWith("(") && contenido.lastIndexOf(")") > 0) {
            contenido = contenido.substring(1, contenido.lastIndexOf(")"));
        }

        return limpiarExpresion(contenido);
    }

    private String extraerEntreParentesis(String linea) {
        int inicio = linea.indexOf("(");
        int fin = linea.lastIndexOf(")");

        if (inicio < 0 || fin < 0 || fin <= inicio) {
            return "";
        }

        return limpiarExpresion(linea.substring(inicio + 1, fin));
    }

    private String limpiarExpresion(String expresion) {
        if (expresion == null) {
            return "";
        }

        String limpia = expresion.trim();

        while (limpia.endsWith(";") || limpia.endsWith(":")) {
            limpia = limpia.substring(0, limpia.length() - 1).trim();
        }

        while (limpia.startsWith("(") && limpia.endsWith(")") && parentesisExternosValidos(limpia)) {
            limpia = limpia.substring(1, limpia.length() - 1).trim();
        }

        return limpia;
    }

    private boolean parentesisExternosValidos(String expresion) {
        int nivel = 0;

        for (int i = 0; i < expresion.length(); i++) {
            char c = expresion.charAt(i);

            if (c == '(') {
                nivel++;
            } else if (c == ')') {
                nivel--;
            }

            if (nivel == 0 && i < expresion.length() - 1) {
                return false;
            }
        }

        return nivel == 0;
    }

    private boolean esCadena(String expresion) {
        return expresion.matches("^\".*\"$");
    }

    private boolean esCaracter(String expresion) {
        return expresion.matches("^'.'$");
    }

    private boolean esNumeroEntero(String expresion) {
        return expresion.matches("^-?\\d+$");
    }

    private boolean esNumeroReal(String expresion) {
        return expresion.matches("^-?\\d+\\.\\d+$");
    }

    private boolean esIdentificadorValido(String texto) {
        return texto.matches("^[a-zA-Z_][a-zA-Z0-9_]*$") && !TIPOS.contains(texto.toUpperCase());
    }

    private String quitarLiterales(String expresion) {
        return expresion
                .replaceAll("\"([^\"\\\\]|\\\\.)*\"", " ")
                .replaceAll("'([^'\\\\]|\\\\.)'", " ");
    }

    private String quitarComentarioLinea(String linea) {
        boolean dentroCadena = false;

        for (int i = 0; i < linea.length() - 1; i++) {
            char actual = linea.charAt(i);

            if (actual == '"') {
                dentroCadena = !dentroCadena;
            }

            if (!dentroCadena && actual == '/' && linea.charAt(i + 1) == '/') {
                return linea.substring(0, i);
            }
        }

        return linea;
    }

    private String eliminarComentariosDeBloque(String codigo) {
        return codigo.replaceAll("(?s)/\\*.*?\\*/", "");
    }

    private void agregarError(int linea, String lexema, String descripcion) {
        errores.add(new ErrorSemantico(linea, lexema, descripcion));
    }

    private ResultadoSemantico construirResultado() {
        boolean correcto = errores.isEmpty();
        String mensaje = correcto
                ? "Análisis semántico correcto."
                : "Se encontraron " + errores.size() + " error(es) semántico(s).";

        return new ResultadoSemantico(correcto, mensaje, errores, tabla.listar());
    }
}
