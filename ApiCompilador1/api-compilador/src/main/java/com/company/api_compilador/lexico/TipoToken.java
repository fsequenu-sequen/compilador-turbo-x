package com.company.api_compilador.lexico;

/**
 * Enum central de tokens del lenguaje Turbo X.
 *
 * Este archivo incluye:
 * 1. Tokens nuevos del lenguaje formal.
 * 2. Tokens antiguos de compatibilidad temporal.
 *
 * ¿Por qué se dejan tokens antiguos?
 * Porque durante el proceso de regeneración puede existir un AnalizadorLexico.java
 * generado con el Lexer.flex anterior. Dejarlos aquí evita errores de compilación.
 */
public enum TipoToken {

    // =========================
    // ESTRUCTURA PRINCIPAL
    // =========================
    PROGRAMA,       // PROGRAMA
    INICIO,         // INICIO
    FIN,            // FIN

    // =========================
    // TIPOS DE DATOS
    // =========================
    TIPO_ENTERO,    // ENTERO
    TIPO_REAL,      // REAL / DECIMAL
    TIPO_CADENA,    // CADENA / TEXTO
    TIPO_CARACTER,  // CARACTER
    TIPO_LOGICO,    // LOGICO / ESTADO

    // =========================
    // VALORES LÓGICOS
    // =========================
    VALOR_VERDADERO, // VERDADERO
    VALOR_FALSO,     // FALSO

    // =========================
    // ENTRADA Y SALIDA
    // =========================
    IMPRIMIR,       // IMPRIMIR
    LEER,           // LEER

    // =========================
    // CONTROL DE FLUJO
    // =========================
    SI,             // SI
    ENTONCES,       // ENTONCES
    SINO,           // SINO
    MIENTRAS,       // MIENTRAS
    HACER,          // HACER

    // =========================
    // ESTRUCTURA TIPO SWITCH EN ESPAÑOL
    // =========================
    EVALUAR,        // EVALUAR
    CASO,           // CASO
    OTRO,           // OTRO
    PARAR,          // PARAR

    // =========================
    // MÓDULO DE GRÁFICAS
    // =========================
    GRAFICAR,       // GRAFICAR

    // =========================
    // IDENTIFICADORES Y LITERALES
    // =========================
    IDENTIFICADOR,  // edad, nombre, x
    NUMERO_ENTERO,  // 10
    NUMERO_REAL,    // 10.5
    VALOR_CADENA,   // "Hola"
    VALOR_CARACTER, // 'A'

    // =========================
    // OPERADORES ARITMÉTICOS
    // =========================
    OP_ASIG,        // =
    OP_SUMA,        // +
    OP_RESTA,       // -
    OP_MULT,        // *
    OP_DIV,         // /
    OP_POT,         // ^ o **
    OP_MOD,         // %

    // =========================
    // OPERADORES RELACIONALES
    // =========================
    IGUAL_QUE,       // ==
    DIFERENTE_QUE,   // !=
    MAYOR_QUE,       // >
    MENOR_QUE,       // <
    MAYOR_IGUAL,     // >=
    MENOR_IGUAL,     // <=

    // =========================
    // OPERADORES LÓGICOS
    // =========================
    OP_Y,            // Y o &&
    OP_O,            // O o ||
    OP_NO,           // NO o !

    // =========================
    // DELIMITADORES
    // =========================
    LLAVE_ABRE,      // {
    LLAVE_CIERRA,    // }
    PARENT_ABRE,     // (
    PARENT_CIERRA,   // )
    FIN_SENTENCIA,   // ;
    COMA,            // ,
    DOS_PUNTOS,      // :

    // =========================
    // COMENTARIOS
    // =========================
    COMENTARIO,

    // =========================
    // ERRORES LÉXICOS
    // =========================
    ERROR_CADENA,
    ERROR_CARACTER,
    ERROR_NUMERO,
    ERROR_IDENTIFICADOR,
    ERROR,

    // =========================
    // FIN DE ARCHIVO
    // =========================
    EOF,

    // =====================================================
    // COMPATIBILIDAD TEMPORAL CON LA VERSIÓN ANTERIOR
    // =====================================================
    INICIO_PROG,
    LLAVE_ABRE_PROG,
    LLAVE_CIERRA_PROG,
    DECLARACION,
    SALIDA,
    ENTRADA,
    PARA,
    HASTA,
    REPETIR,
    OTROS,
    FUNCION,
    TIPO_DECIMAL,
    TIPO_TEXTO,
    TIPO_ESTADO,
    VALOR_VERDAD,
    TITULO_PROG,
    NUMERO_DECIMAL
}
