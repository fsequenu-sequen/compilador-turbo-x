package com.company.api_compilador.semantico;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnalizadorSemantico {

    private final TablaSimbolos tabla = new TablaSimbolos();
    private final List<ErrorSemantico> errores = new ArrayList<>();
    private final Set<String> erroresDivisionCeroEmitidos = new HashSet<>();

    private static final Set<String> TIPOS = new HashSet<>(Arrays.asList(
            "ENTERO", "REAL", "CADENA", "CARACTER", "LOGICO"
    ));

    private static final Set<String> FUNCIONES_MATEMATICAS = new HashSet<>(Arrays.asList(
            "SIN", "SEN", "COS", "TAN",
            "SQRT", "RAIZ",
            "ABS", "ABSOLUTO",
            "LOG", "LN", "EXP",
            "MAX", "MIN"
    ));

    private static final Set<String> CONSTANTES_MATEMATICAS = new HashSet<>(Arrays.asList(
            "PI", "E"
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

        if (FUNCIONES_MATEMATICAS.contains(nombre.toUpperCase()) || CONSTANTES_MATEMATICAS.contains(nombre.toUpperCase())) {
            agregarError(numeroLinea, nombre, "No se recomienda usar el nombre de una función o constante matemática como variable.");
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

        if (!"LOGICO".equals(tipoCondicion) && !"DESCONOCIDO".equals(tipoCondicion)) {
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

        String tipo = inferirTipo(expresion, numeroLinea, "evaluar");
        if ("REAL".equals(tipo) || "DESCONOCIDO".equals(tipo)) {
            return;
        }

        if (!"ENTERO".equals(tipo) && !"CADENA".equals(tipo) && !"CARACTER".equals(tipo) && !"LOGICO".equals(tipo)) {
            agregarError(numeroLinea, expresion,
                    "La instrucción EVALUAR usa un tipo no válido: " + tipo + ".");
        }
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

        if (CONSTANTES_MATEMATICAS.contains(expresion.toUpperCase())) {
            return "REAL";
        }

        if (esNumeroEntero(expresion)) {
            return "ENTERO";
        }

        if (esNumeroReal(expresion)) {
            return "REAL";
        }

        if (expresion.startsWith("NO ")) {
            String tipo = inferirTipo(expresion.substring(3), numeroLinea, contexto);
            if (!"LOGICO".equals(tipo) && !"DESCONOCIDO".equals(tipo)) {
                agregarError(numeroLinea, expresion, "El operador NO solo puede aplicarse a expresiones LOGICAS.");
            }
            return "LOGICO";
        }

        if (expresion.startsWith("!")) {
            String tipo = inferirTipo(expresion.substring(1), numeroLinea, contexto);
            if (!"LOGICO".equals(tipo) && !"DESCONOCIDO".equals(tipo)) {
                agregarError(numeroLinea, expresion, "El operador ! solo puede aplicarse a expresiones LOGICAS.");
            }
            return "LOGICO";
        }

        LlamadaFuncion llamada = extraerLlamadaFuncion(expresion);
        if (llamada != null) {
            return inferirTipoFuncion(llamada, numeroLinea);
        }

        Operacion opLogica = encontrarOperadorLogicoPrincipal(expresion);
        if (opLogica != null) {
            String tipoIzq = inferirTipo(opLogica.izquierda, numeroLinea, contexto);
            String tipoDer = inferirTipo(opLogica.derecha, numeroLinea, contexto);

            if (!"LOGICO".equals(tipoIzq) && !"DESCONOCIDO".equals(tipoIzq)) {
                agregarError(numeroLinea, opLogica.izquierda,
                        "Los operadores lógicos solo pueden trabajar con expresiones LOGICAS.");
            }

            if (!"LOGICO".equals(tipoDer) && !"DESCONOCIDO".equals(tipoDer)) {
                agregarError(numeroLinea, opLogica.derecha,
                        "Los operadores lógicos solo pueden trabajar con expresiones LOGICAS.");
            }

            return "LOGICO";
        }

        Operacion opRelacional = encontrarOperadorRelacionalPrincipal(expresion);
        if (opRelacional != null) {
            return inferirTipoRelacional(opRelacional, numeroLinea);
        }

        Operacion opAritmetica = encontrarOperadorAritmeticoPrincipal(expresion);
        if (opAritmetica != null) {
            return inferirTipoAritmetico(opAritmetica, numeroLinea);
        }

        if (esIdentificadorValido(expresion)) {
            return tipoDeIdentificador(expresion, numeroLinea);
        }

        validarIdentificadores(expresion, numeroLinea);
        return "DESCONOCIDO";
    }

    private String inferirTipoConVariableLocalX(String expresion, int numeroLinea) {
        if (!tabla.existe("x")) {
            tabla.declarar(new Simbolo("x", "REAL", "VARIABLE_LOCAL_GRAFICA", numeroLinea, true));
        }

        return inferirTipo(expresion, numeroLinea, "graficar");
    }

    private String inferirTipoFuncion(LlamadaFuncion llamada, int numeroLinea) {
        String nombre = llamada.nombre.toUpperCase();

        if (!FUNCIONES_MATEMATICAS.contains(nombre)) {
            agregarError(numeroLinea, llamada.nombre,
                    "La función '" + llamada.nombre + "' no está definida en Turbo X.");
            return "DESCONOCIDO";
        }

        if (llamada.argumentos.isEmpty()) {
            agregarError(numeroLinea, llamada.nombre,
                    "La función '" + llamada.nombre + "' necesita al menos un argumento.");
            return "DESCONOCIDO";
        }

        boolean aceptaMultiples = nombre.equals("MAX") || nombre.equals("MIN");

        if (!aceptaMultiples && llamada.argumentos.size() != 1) {
            agregarError(numeroLinea, llamada.nombre,
                    "La función '" + llamada.nombre + "' recibe exactamente un argumento.");
        }

        for (String arg : llamada.argumentos) {
            String tipoArg = inferirTipo(arg, numeroLinea, "funcion");

            if (!esNumerico(tipoArg) && !"DESCONOCIDO".equals(tipoArg)) {
                agregarError(numeroLinea, arg,
                        "La función matemática '" + llamada.nombre + "' solo acepta argumentos numéricos.");
            }
        }

        return "REAL";
    }

    private String inferirTipoRelacional(Operacion op, int numeroLinea) {
        String tipoIzquierda = inferirTipo(op.izquierda, numeroLinea, "relacional");
        String tipoDerecha = inferirTipo(op.derecha, numeroLinea, "relacional");

        if (op.operador.equals(">") || op.operador.equals("<") || op.operador.equals(">=") || op.operador.equals("<=")) {
            if ((!esNumerico(tipoIzquierda) && !"DESCONOCIDO".equals(tipoIzquierda))
                    || (!esNumerico(tipoDerecha) && !"DESCONOCIDO".equals(tipoDerecha))) {
                agregarError(numeroLinea, op.izquierda + " " + op.operador + " " + op.derecha,
                        "Los operadores " + op.operador + " solo pueden comparar valores numéricos.");
            }
        }

        if (op.operador.equals("==") || op.operador.equals("!=")) {
            boolean compatibles = tipoIzquierda.equals(tipoDerecha)
                    || (esNumerico(tipoIzquierda) && esNumerico(tipoDerecha))
                    || "DESCONOCIDO".equals(tipoIzquierda)
                    || "DESCONOCIDO".equals(tipoDerecha);

            if (!compatibles) {
                agregarError(numeroLinea, op.izquierda + " " + op.operador + " " + op.derecha,
                        "No se pueden comparar valores de tipo " + tipoIzquierda + " y " + tipoDerecha + ".");
            }
        }

        return "LOGICO";
    }

    private String inferirTipoAritmetico(Operacion op, int numeroLinea) {
        String tipoIzquierda = inferirTipo(op.izquierda, numeroLinea, "aritmetico");
        String tipoDerecha = inferirTipo(op.derecha, numeroLinea, "aritmetico");

        /*
         * Turbo X ahora permite concatenación formal con el operador +.
         * Ejemplo válido:
         *      IMPRIMIR("su edad es " + edad);
         *
         * Regla aplicada:
         * - Si el operador es + y al menos un lado es CADENA o CARACTER,
         *   la expresión resultante es CADENA.
         * - Para -, *, /, %, ^ se siguen exigiendo operandos numéricos.
         */
        if (op.operador.equals("+")) {
            boolean izquierdaTexto = esTexto(tipoIzquierda);
            boolean derechaTexto = esTexto(tipoDerecha);

            if (izquierdaTexto || derechaTexto) {
                return "CADENA";
            }
        }

        if (!esNumerico(tipoIzquierda) && !"DESCONOCIDO".equals(tipoIzquierda)) {
            agregarError(numeroLinea, op.izquierda,
                    "La operación '" + op.operador + "' contiene un valor de tipo " + tipoIzquierda
                            + ", pero se esperaba ENTERO o REAL. Para unir texto con valores use el operador + con una CADENA.");
        }

        if (!esNumerico(tipoDerecha) && !"DESCONOCIDO".equals(tipoDerecha)) {
            agregarError(numeroLinea, op.derecha,
                    "La operación '" + op.operador + "' contiene un valor de tipo " + tipoDerecha
                            + ", pero se esperaba ENTERO o REAL. Para unir texto con valores use el operador + con una CADENA.");
        }

        if (op.operador.equals("/") || op.operador.equals("%")) {
            if (esExpresionConstanteCero(op.derecha)) {
                String clave = numeroLinea + ":" + op.operador + ":" + op.derecha;
                if (!erroresDivisionCeroEmitidos.contains(clave)) {
                    erroresDivisionCeroEmitidos.add(clave);
                    String nombreOperacion = op.operador.equals("/") ? "división" : "módulo";
                    agregarError(numeroLinea, op.derecha,
                            "No se permite " + nombreOperacion + " entre cero. El divisor evaluado es 0.");
                }
            }
        }

        if (op.operador.equals("/")) {
            return "REAL";
        }

        if ("REAL".equals(tipoIzquierda) || "REAL".equals(tipoDerecha)) {
            return "REAL";
        }

        return "ENTERO";
    }

    private boolean esTexto(String tipo) {
        return "CADENA".equals(tipo) || "CARACTER".equals(tipo);
    }

    private void validarIdentificadores(String expresion, int numeroLinea) {
        String expresionSinLiterales = quitarLiterales(expresion);
        Matcher matcher = PATRON_IDENTIFICADOR.matcher(expresionSinLiterales);

        while (matcher.find()) {
            String posibleIdentificador = matcher.group();
            String mayuscula = posibleIdentificador.toUpperCase();

            if (PALABRAS_RESERVADAS.contains(mayuscula)
                    || FUNCIONES_MATEMATICAS.contains(mayuscula)
                    || CONSTANTES_MATEMATICAS.contains(mayuscula)) {
                continue;
            }

            if (esNombreDeFuncionEnExpresion(expresionSinLiterales, matcher.end())) {
                continue;
            }

            if (!tabla.existe(posibleIdentificador)) {
                agregarError(numeroLinea, posibleIdentificador,
                        "La variable '" + posibleIdentificador + "' se está usando antes de ser declarada.");
            }
        }
    }

    private String tipoDeIdentificador(String nombre, int numeroLinea) {
        if (CONSTANTES_MATEMATICAS.contains(nombre.toUpperCase())) {
            return "REAL";
        }

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

    private Operacion encontrarOperadorLogicoPrincipal(String expresion) {
        return encontrarOperadorPalabraPrincipal(expresion, new String[]{"&&", "||", " Y ", " O "});
    }

    private Operacion encontrarOperadorRelacionalPrincipal(String expresion) {
        return encontrarOperadorPrincipal(expresion, new String[]{">=", "<=", "==", "!=", ">", "<"}, false);
    }

    private Operacion encontrarOperadorAritmeticoPrincipal(String expresion) {
        Operacion sumaResta = encontrarOperadorPrincipal(expresion, new String[]{"+", "-"}, true);
        if (sumaResta != null) {
            return sumaResta;
        }

        Operacion multDiv = encontrarOperadorPrincipal(expresion, new String[]{"*", "/", "%"}, true);
        if (multDiv != null) {
            return multDiv;
        }

        return encontrarOperadorPrincipal(expresion, new String[]{"^"}, true);
    }

    private Operacion encontrarOperadorPalabraPrincipal(String expresion, String[] operadores) {
        int nivel = 0;
        boolean dentroCadena = false;
        boolean dentroCaracter = false;

        for (int i = expresion.length() - 1; i >= 0; i--) {
            char c = expresion.charAt(i);

            if (c == '"' && !dentroCaracter) {
                dentroCadena = !dentroCadena;
            }

            if (c == '\'' && !dentroCadena) {
                dentroCaracter = !dentroCaracter;
            }

            if (dentroCadena || dentroCaracter) {
                continue;
            }

            if (c == ')') {
                nivel++;
            } else if (c == '(') {
                nivel--;
            }

            if (nivel != 0) {
                continue;
            }

            for (String operador : operadores) {
                int inicio = i - operador.length() + 1;
                if (inicio >= 0 && expresion.substring(inicio, i + 1).equals(operador)) {
                    return new Operacion(
                            limpiarExpresion(expresion.substring(0, inicio)),
                            operador.trim(),
                            limpiarExpresion(expresion.substring(i + 1))
                    );
                }
            }
        }

        return null;
    }

    private Operacion encontrarOperadorPrincipal(String expresion, String[] operadores, boolean evitarUnario) {
        int nivel = 0;
        boolean dentroCadena = false;
        boolean dentroCaracter = false;

        for (int i = expresion.length() - 1; i >= 0; i--) {
            char c = expresion.charAt(i);

            if (c == '"' && !dentroCaracter) {
                dentroCadena = !dentroCadena;
            }

            if (c == '\'' && !dentroCadena) {
                dentroCaracter = !dentroCaracter;
            }

            if (dentroCadena || dentroCaracter) {
                continue;
            }

            if (c == ')') {
                nivel++;
            } else if (c == '(') {
                nivel--;
            }

            if (nivel != 0) {
                continue;
            }

            for (String operador : operadores) {
                int inicio = i - operador.length() + 1;
                if (inicio < 0) {
                    continue;
                }

                if (!expresion.substring(inicio, i + 1).equals(operador)) {
                    continue;
                }

                if (evitarUnario && (operador.equals("+") || operador.equals("-")) && esSignoUnario(expresion, inicio)) {
                    continue;
                }

                String izquierda = limpiarExpresion(expresion.substring(0, inicio));
                String derecha = limpiarExpresion(expresion.substring(i + 1));

                if (izquierda.isEmpty() || derecha.isEmpty()) {
                    continue;
                }

                return new Operacion(izquierda, operador, derecha);
            }
        }

        return null;
    }

    private boolean esSignoUnario(String expresion, int posicion) {
        if (posicion == 0) {
            return true;
        }

        int anterior = posicion - 1;
        while (anterior >= 0 && Character.isWhitespace(expresion.charAt(anterior))) {
            anterior--;
        }

        if (anterior < 0) {
            return true;
        }

        char c = expresion.charAt(anterior);
        return c == '(' || c == ',' || c == '+' || c == '-' || c == '*' || c == '/' || c == '%' || c == '^'
                || c == '=' || c == '<' || c == '>' || c == '!';
    }

    private LlamadaFuncion extraerLlamadaFuncion(String expresion) {
        Matcher matcher = Pattern.compile("^([a-zA-Z_][a-zA-Z0-9_]*)\\s*\\((.*)\\)$").matcher(expresion);

        if (!matcher.matches()) {
            return null;
        }

        if (!parentesisDeFuncionValidos(expresion, matcher.start(2) - 1)) {
            return null;
        }

        String nombre = matcher.group(1);
        String contenido = matcher.group(2);
        return new LlamadaFuncion(nombre, separarArgumentos(contenido));
    }

    private boolean parentesisDeFuncionValidos(String expresion, int indiceParentesis) {
        int nivel = 0;

        for (int i = indiceParentesis; i < expresion.length(); i++) {
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

    private List<String> separarArgumentos(String contenido) {
        List<String> argumentos = new ArrayList<>();

        if (contenido == null || contenido.trim().isEmpty()) {
            return argumentos;
        }

        int nivel = 0;
        int inicio = 0;
        boolean dentroCadena = false;
        boolean dentroCaracter = false;

        for (int i = 0; i < contenido.length(); i++) {
            char c = contenido.charAt(i);

            if (c == '"' && !dentroCaracter) {
                dentroCadena = !dentroCadena;
            }

            if (c == '\'' && !dentroCadena) {
                dentroCaracter = !dentroCaracter;
            }

            if (dentroCadena || dentroCaracter) {
                continue;
            }

            if (c == '(') {
                nivel++;
            } else if (c == ')') {
                nivel--;
            } else if (c == ',' && nivel == 0) {
                argumentos.add(limpiarExpresion(contenido.substring(inicio, i)));
                inicio = i + 1;
            }
        }

        argumentos.add(limpiarExpresion(contenido.substring(inicio)));
        return argumentos;
    }

    private boolean esNombreDeFuncionEnExpresion(String expresion, int finIdentificador) {
        int i = finIdentificador;

        while (i < expresion.length() && Character.isWhitespace(expresion.charAt(i))) {
            i++;
        }

        return i < expresion.length() && expresion.charAt(i) == '(';
    }

    private boolean esExpresionConstanteCero(String expresion) {
        try {
            Double valor = new EvaluadorConstante(expresion).parsear();
            return valor != null && Math.abs(valor) < 0.0000000001;
        } catch (Exception ex) {
            return false;
        }
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
        boolean dentroCadena = false;
        boolean dentroCaracter = false;

        for (int i = 0; i < expresion.length(); i++) {
            char c = expresion.charAt(i);

            if (c == '"' && !dentroCaracter) {
                dentroCadena = !dentroCadena;
            }

            if (c == '\'' && !dentroCadena) {
                dentroCaracter = !dentroCaracter;
            }

            if (dentroCadena || dentroCaracter) {
                continue;
            }

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

    private static class Operacion {
        private final String izquierda;
        private final String operador;
        private final String derecha;

        private Operacion(String izquierda, String operador, String derecha) {
            this.izquierda = izquierda;
            this.operador = operador;
            this.derecha = derecha;
        }
    }

    private static class LlamadaFuncion {
        private final String nombre;
        private final List<String> argumentos;

        private LlamadaFuncion(String nombre, List<String> argumentos) {
            this.nombre = nombre;
            this.argumentos = argumentos;
        }
    }

    /**
     * Evaluador simple para expresiones constantes.
     * Se usa solo para detectar divisores como 0, (5 - 5), 2 * 0, etc.
     * Si encuentra variables, lanza excepción y el semántico no reporta división entre cero.
     */
    private static class EvaluadorConstante {

        private final String entrada;
        private int pos = 0;

        private EvaluadorConstante(String entrada) {
            this.entrada = entrada == null ? "" : entrada.replace("**", "^");
        }

        private Double parsear() {
            double valor = expresion();
            saltarEspacios();

            if (pos < entrada.length()) {
                throw new IllegalArgumentException("Expresión no constante.");
            }

            return valor;
        }

        private double expresion() {
            double valor = termino();

            while (true) {
                saltarEspacios();

                if (coincide('+')) {
                    valor += termino();
                } else if (coincide('-')) {
                    valor -= termino();
                } else {
                    return valor;
                }
            }
        }

        private double termino() {
            double valor = potencia();

            while (true) {
                saltarEspacios();

                if (coincide('*')) {
                    valor *= potencia();
                } else if (coincide('/')) {
                    double divisor = potencia();
                    if (Math.abs(divisor) < 0.0000000001) {
                        throw new IllegalArgumentException("División entre cero en constante.");
                    }
                    valor /= divisor;
                } else if (coincide('%')) {
                    double divisor = potencia();
                    if (Math.abs(divisor) < 0.0000000001) {
                        throw new IllegalArgumentException("Módulo entre cero en constante.");
                    }
                    valor %= divisor;
                } else {
                    return valor;
                }
            }
        }

        private double potencia() {
            double valor = factor();
            saltarEspacios();

            if (coincide('^')) {
                valor = Math.pow(valor, potencia());
            }

            return valor;
        }

        private double factor() {
            saltarEspacios();

            if (coincide('+')) {
                return factor();
            }

            if (coincide('-')) {
                return -factor();
            }

            if (coincide('(')) {
                double valor = expresion();
                if (!coincide(')')) {
                    throw new IllegalArgumentException("Paréntesis no cerrado.");
                }
                return valor;
            }

            if (pos < entrada.length() && (Character.isLetter(entrada.charAt(pos)) || entrada.charAt(pos) == '_')) {
                String nombre = leerIdentificador().toUpperCase(Locale.ROOT);
                saltarEspacios();

                if (nombre.equals("PI")) {
                    return Math.PI;
                }

                if (nombre.equals("E")) {
                    return Math.E;
                }

                if (!coincide('(')) {
                    throw new IllegalArgumentException("Variable no constante.");
                }

                double argumento = expresion();

                if (!coincide(')')) {
                    throw new IllegalArgumentException("Función no cerrada.");
                }

                switch (nombre) {
                    case "SIN":
                    case "SEN":
                        return Math.sin(argumento);
                    case "COS":
                        return Math.cos(argumento);
                    case "TAN":
                        return Math.tan(argumento);
                    case "SQRT":
                    case "RAIZ":
                        return Math.sqrt(argumento);
                    case "ABS":
                    case "ABSOLUTO":
                        return Math.abs(argumento);
                    case "LOG":
                        return Math.log10(argumento);
                    case "LN":
                        return Math.log(argumento);
                    case "EXP":
                        return Math.exp(argumento);
                    default:
                        throw new IllegalArgumentException("Función no constante.");
                }
            }

            return leerNumero();
        }

        private double leerNumero() {
            saltarEspacios();

            int inicio = pos;
            boolean punto = false;

            while (pos < entrada.length()) {
                char c = entrada.charAt(pos);

                if (Character.isDigit(c)) {
                    pos++;
                } else if (c == '.' && !punto) {
                    punto = true;
                    pos++;
                } else {
                    break;
                }
            }

            if (inicio == pos) {
                throw new IllegalArgumentException("Número esperado.");
            }

            return Double.parseDouble(entrada.substring(inicio, pos));
        }

        private String leerIdentificador() {
            int inicio = pos;

            while (pos < entrada.length()
                    && (Character.isLetterOrDigit(entrada.charAt(pos)) || entrada.charAt(pos) == '_')) {
                pos++;
            }

            return entrada.substring(inicio, pos);
        }

        private boolean coincide(char esperado) {
            saltarEspacios();

            if (pos < entrada.length() && entrada.charAt(pos) == esperado) {
                pos++;
                return true;
            }

            return false;
        }

        private void saltarEspacios() {
            while (pos < entrada.length() && Character.isWhitespace(entrada.charAt(pos))) {
                pos++;
            }
        }
    }
}
