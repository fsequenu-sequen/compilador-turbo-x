/** Chimaltenango 30 de mayo 2026
Proyecto Final Compiladores
Integrantes: 
1990-23-4406	Christopher Obryan Mazariegos Crúz
1990-23-17188	Luis Miguel Vaquiax Camey
1990-23-10442	Keyner Alejandro Rivera Axpuac
1990-23-22934	Freyder José Sequén Urlao
*/
/*
 * COMENTARIO GENERAL:
 * Especificación JFlex del lexer usado por JCUP.
 * A diferencia del lexer léxico visual, este devuelve Symbol para que ParserCup
 * pueda validar la gramática del lenguaje Turbo X.
 */


/**
 * LexerCup.flex
 * Analizador léxico para alimentar el parser JCUP.
 *
 * Esta versión evita reglas problemáticas de JFlex y usa estados
 * para comentarios de bloque.
 */

package com.company.api_compilador.sintactico;

import java_cup.runtime.Symbol;

%%

%public
%class LexerCup
%unicode
%line
%column
%implements java_cup.runtime.Scanner
%function next_token
%type java_cup.runtime.Symbol
%state COMENTARIO_BLOQUE

%{
    private Symbol s(int tipo) {
        return new Symbol(tipo, yyline + 1, yycolumn + 1);
    }

    private Symbol s(int tipo, Object valor) {
        return new Symbol(tipo, yyline + 1, yycolumn + 1, valor);
    }
%}

%eofval{
    return s(SimbolosCup.EOF);
%eofval}

/* =========================
   MACROS DEL LENGUAJE
   ========================= */

EspacioBlanco   = \r\n | \r | \n | [ \t\f]+
ComentarioLinea = "//" [^\r\n]*

Letra           = [A-Za-z_]
Digito          = [0-9]
Identificador   = {Letra}({Letra}|{Digito})*

NumeroEntero    = 0 | [1-9][0-9]*
NumeroReal      = {NumeroEntero} "." [0-9]+

Cadena          = \"([^\"\\\r\n]|\\.)*\"
Caracter        = \'([^\'\\\r\n]|\\.)\'

%%

<YYINITIAL> {

    /* =========================
       ESPACIOS Y COMENTARIOS
       ========================= */

    {EspacioBlanco}      { /* Ignorar */ }

    {ComentarioLinea}    { /* Ignorar comentarios de línea */ }

    "/*"                 { yybegin(COMENTARIO_BLOQUE); }


    /* =========================
       ERROR DE NÚMERO REAL INCOMPLETO
       ========================= */

    {NumeroEntero} "."   { return s(SimbolosCup.ERROR_NUMERO, yytext()); }


    /* =========================
       PALABRAS RESERVADAS
       ========================= */

    ("PROGRAMA"|"Programa"|"programa")
        { return s(SimbolosCup.PROGRAMA, yytext()); }

    ("INICIO"|"Inicio"|"inicio")
        { return s(SimbolosCup.INICIO, yytext()); }

    ("FIN"|"Fin"|"fin")
        { return s(SimbolosCup.FIN, yytext()); }


    /* =========================
       TIPOS DE DATOS
       ========================= */

    ("ENTERO"|"Entero"|"entero")
        { return s(SimbolosCup.TIPO_ENTERO, yytext()); }

    ("REAL"|"Real"|"real"|"DECIMAL"|"Decimal"|"decimal")
        { return s(SimbolosCup.TIPO_REAL, yytext()); }

    ("CADENA"|"Cadena"|"cadena"|"TEXTO"|"Texto"|"texto")
        { return s(SimbolosCup.TIPO_CADENA, yytext()); }

    ("CARACTER"|"Caracter"|"caracter"|"CARÁCTER"|"Carácter"|"carácter")
        { return s(SimbolosCup.TIPO_CARACTER, yytext()); }

    ("LOGICO"|"Logico"|"logico"|"LÓGICO"|"Lógico"|"lógico")
        { return s(SimbolosCup.TIPO_LOGICO, yytext()); }


    /* =========================
       VALORES LÓGICOS
       ========================= */

    ("VERDADERO"|"Verdadero"|"verdadero")
        { return s(SimbolosCup.VALOR_VERDADERO, yytext()); }

    ("FALSO"|"Falso"|"falso")
        { return s(SimbolosCup.VALOR_FALSO, yytext()); }


    /* =========================
       ENTRADA Y SALIDA
       ========================= */

    ("IMPRIMIR"|"Imprimir"|"imprimir")
        { return s(SimbolosCup.IMPRIMIR, yytext()); }

    ("LEER"|"Leer"|"leer")
        { return s(SimbolosCup.LEER, yytext()); }


    /* =========================
       CONTROL DE FLUJO
       ========================= */

    ("SI"|"Si"|"si")
        { return s(SimbolosCup.SI, yytext()); }

    ("ENTONCES"|"Entonces"|"entonces")
        { return s(SimbolosCup.ENTONCES, yytext()); }

    ("SINO"|"Sino"|"sino")
        { return s(SimbolosCup.SINO, yytext()); }

    ("MIENTRAS"|"Mientras"|"mientras")
        { return s(SimbolosCup.MIENTRAS, yytext()); }

    ("HACER"|"Hacer"|"hacer")
        { return s(SimbolosCup.HACER, yytext()); }


    /* =========================
       ESTRUCTURA EVALUAR / CASO
       ========================= */

    ("EVALUAR"|"Evaluar"|"evaluar")
        { return s(SimbolosCup.EVALUAR, yytext()); }

    ("CASO"|"Caso"|"caso")
        { return s(SimbolosCup.CASO, yytext()); }

    ("OTRO"|"Otro"|"otro"|"OTROS"|"Otros"|"otros")
        { return s(SimbolosCup.OTRO, yytext()); }

    ("PARAR"|"Parar"|"parar")
        { return s(SimbolosCup.PARAR, yytext()); }


    /* =========================
       GRÁFICAS
       ========================= */

    ("GRAFICAR"|"Graficar"|"graficar")
        { return s(SimbolosCup.GRAFICAR, yytext()); }


    /* =========================
       OPERADORES LÓGICOS
       ========================= */

    ("Y"|"y"|"&&")
        { return s(SimbolosCup.OP_Y, yytext()); }

    ("O"|"o"|"||")
        { return s(SimbolosCup.OP_O, yytext()); }

    ("NO"|"No"|"no"|"!")
        { return s(SimbolosCup.OP_NO, yytext()); }


    /* =========================
       OPERADORES RELACIONALES
       ========================= */

    "=="    { return s(SimbolosCup.IGUAL_QUE, yytext()); }
    "!="    { return s(SimbolosCup.DIFERENTE_QUE, yytext()); }
    ">="    { return s(SimbolosCup.MAYOR_IGUAL, yytext()); }
    "<="    { return s(SimbolosCup.MENOR_IGUAL, yytext()); }
    ">"     { return s(SimbolosCup.MAYOR_QUE, yytext()); }
    "<"     { return s(SimbolosCup.MENOR_QUE, yytext()); }


    /* =========================
       OPERADORES ARITMÉTICOS
       ========================= */

    "**"    { return s(SimbolosCup.OP_POT, yytext()); }
    "^"     { return s(SimbolosCup.OP_POT, yytext()); }
    "="     { return s(SimbolosCup.OP_ASIG, yytext()); }
    "+"     { return s(SimbolosCup.OP_SUMA, yytext()); }
    "-"     { return s(SimbolosCup.OP_RESTA, yytext()); }
    "*"     { return s(SimbolosCup.OP_MULT, yytext()); }
    "/"     { return s(SimbolosCup.OP_DIV, yytext()); }
    "%"     { return s(SimbolosCup.OP_MOD, yytext()); }


    /* =========================
       DELIMITADORES
       ========================= */

    "{"     { return s(SimbolosCup.LLAVE_ABRE, yytext()); }
    "}"     { return s(SimbolosCup.LLAVE_CIERRA, yytext()); }
    "("     { return s(SimbolosCup.PARENT_ABRE, yytext()); }
    ")"     { return s(SimbolosCup.PARENT_CIERRA, yytext()); }
    ";"     { return s(SimbolosCup.FIN_SENTENCIA, yytext()); }
    ","     { return s(SimbolosCup.COMA, yytext()); }
    ":"     { return s(SimbolosCup.DOS_PUNTOS, yytext()); }


    /* =========================
       LITERALES
       ========================= */

    {NumeroReal}      { return s(SimbolosCup.NUMERO_REAL, yytext()); }
    {NumeroEntero}    { return s(SimbolosCup.NUMERO_ENTERO, yytext()); }
    {Cadena}          { return s(SimbolosCup.VALOR_CADENA, yytext()); }
    {Caracter}        { return s(SimbolosCup.VALOR_CARACTER, yytext()); }


    /* =========================
       IDENTIFICADORES
       ========================= */

    {Identificador}   { return s(SimbolosCup.IDENTIFICADOR, yytext()); }


    /* =========================
       ERROR GENERAL
       ========================= */

    .                 { return s(SimbolosCup.ERROR, yytext()); }
}

/* =========================
   ESTADO PARA COMENTARIOS DE BLOQUE
   ========================= */

<COMENTARIO_BLOQUE> {
    "*/"              { yybegin(YYINITIAL); }
    [^]               { /* Ignorar cualquier carácter dentro del comentario */ }
}
