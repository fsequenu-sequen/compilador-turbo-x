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
 * Especificación JFlex del analizador léxico principal.
 * Aquí se definen palabras reservadas, operadores, literales, comentarios y
 * errores léxicos. Al ejecutarse JFlex, esta especificación genera
 * AnalizadorLexico.java.
 */


/**
 * Lexer.flex
 * Analizador léxico principal para la tabla de tokens de la interfaz web.
 *
 * Este lexer se usa en:
 *   /api/analizar
 *
 * IMPORTANTE:
 * Este archivo genera:
 *   AnalizadorLexico.java
 *
 * No confundir con LexerCup.flex, que se usa para JCUP.
 */

package com.company.api_compilador.lexico;

import com.company.api_compilador.lexico.Token;
import com.company.api_compilador.lexico.TipoToken;

%%

%public
%class AnalizadorLexico
%unicode
%line
%column
%type Token
%function proximoToken
%state COMENTARIO_BLOQUE

%{
    private Token t(TipoToken tipo, String lexema) {
        return new Token(tipo, lexema, yyline + 1, yycolumn + 1);
    }
%}

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

    {EspacioBlanco}      { /* Ignorar espacios */ }

    {ComentarioLinea}    { return t(TipoToken.COMENTARIO, yytext()); }

    "/*"                 { yybegin(COMENTARIO_BLOQUE); }


    /* =========================
       ERRORES LÉXICOS ESPECÍFICOS
       ========================= */

    {NumeroEntero} "."   { return t(TipoToken.ERROR_NUMERO, yytext()); }


    /* =========================
       PALABRAS RESERVADAS
       ========================= */

    ("PROGRAMA"|"Programa"|"programa")
        { return t(TipoToken.PROGRAMA, yytext()); }

    ("INICIO"|"Inicio"|"inicio")
        { return t(TipoToken.INICIO, yytext()); }

    ("FIN"|"Fin"|"fin")
        { return t(TipoToken.FIN, yytext()); }


    /* =========================
       TIPOS DE DATOS
       ========================= */

    ("ENTERO"|"Entero"|"entero")
        { return t(TipoToken.TIPO_ENTERO, yytext()); }

    ("REAL"|"Real"|"real"|"DECIMAL"|"Decimal"|"decimal")
        { return t(TipoToken.TIPO_REAL, yytext()); }

    ("CADENA"|"Cadena"|"cadena"|"TEXTO"|"Texto"|"texto")
        { return t(TipoToken.TIPO_CADENA, yytext()); }

    ("CARACTER"|"Caracter"|"caracter"|"CARÁCTER"|"Carácter"|"carácter")
        { return t(TipoToken.TIPO_CARACTER, yytext()); }

    ("LOGICO"|"Logico"|"logico"|"LÓGICO"|"Lógico"|"lógico")
        { return t(TipoToken.TIPO_LOGICO, yytext()); }


    /* =========================
       VALORES LÓGICOS
       ========================= */

    ("VERDADERO"|"Verdadero"|"verdadero")
        { return t(TipoToken.VALOR_VERDADERO, yytext()); }

    ("FALSO"|"Falso"|"falso")
        { return t(TipoToken.VALOR_FALSO, yytext()); }


    /* =========================
       ENTRADA Y SALIDA
       ========================= */

    ("IMPRIMIR"|"Imprimir"|"imprimir")
        { return t(TipoToken.IMPRIMIR, yytext()); }

    ("LEER"|"Leer"|"leer")
        { return t(TipoToken.LEER, yytext()); }


    /* =========================
       CONTROL DE FLUJO
       ========================= */

    ("SI"|"Si"|"si")
        { return t(TipoToken.SI, yytext()); }

    ("ENTONCES"|"Entonces"|"entonces")
        { return t(TipoToken.ENTONCES, yytext()); }

    ("SINO"|"Sino"|"sino")
        { return t(TipoToken.SINO, yytext()); }

    ("MIENTRAS"|"Mientras"|"mientras")
        { return t(TipoToken.MIENTRAS, yytext()); }

    ("HACER"|"Hacer"|"hacer")
        { return t(TipoToken.HACER, yytext()); }


    /* =========================
       ESTRUCTURA TIPO SWITCH EN ESPAÑOL
       ========================= */

    ("EVALUAR"|"Evaluar"|"evaluar")
        { return t(TipoToken.EVALUAR, yytext()); }

    ("CASO"|"Caso"|"caso")
        { return t(TipoToken.CASO, yytext()); }

    ("OTRO"|"Otro"|"otro"|"OTROS"|"Otros"|"otros")
        { return t(TipoToken.OTRO, yytext()); }

    ("PARAR"|"Parar"|"parar")
        { return t(TipoToken.PARAR, yytext()); }


    /* =========================
       GRÁFICAS
       ========================= */

    ("GRAFICAR"|"Graficar"|"graficar")
        { return t(TipoToken.GRAFICAR, yytext()); }


    /* =========================
       OPERADORES LÓGICOS
       ========================= */

    ("Y"|"y"|"&&")
        { return t(TipoToken.OP_Y, yytext()); }

    ("O"|"o"|"||")
        { return t(TipoToken.OP_O, yytext()); }

    ("NO"|"No"|"no"|"!")
        { return t(TipoToken.OP_NO, yytext()); }


    /* =========================
       OPERADORES RELACIONALES
       IMPORTANTE: los de 2 caracteres van antes que los de 1.
       ========================= */

    "=="    { return t(TipoToken.IGUAL_QUE, yytext()); }
    "!="    { return t(TipoToken.DIFERENTE_QUE, yytext()); }
    ">="    { return t(TipoToken.MAYOR_IGUAL, yytext()); }
    "<="    { return t(TipoToken.MENOR_IGUAL, yytext()); }
    ">"     { return t(TipoToken.MAYOR_QUE, yytext()); }
    "<"     { return t(TipoToken.MENOR_QUE, yytext()); }


    /* =========================
       OPERADORES ARITMÉTICOS
       IMPORTANTE: ** va antes que *
       ========================= */

    "**"    { return t(TipoToken.OP_POT, yytext()); }
    "^"     { return t(TipoToken.OP_POT, yytext()); }
    "="     { return t(TipoToken.OP_ASIG, yytext()); }
    "+"     { return t(TipoToken.OP_SUMA, yytext()); }
    "-"     { return t(TipoToken.OP_RESTA, yytext()); }
    "*"     { return t(TipoToken.OP_MULT, yytext()); }
    "/"     { return t(TipoToken.OP_DIV, yytext()); }
    "%"     { return t(TipoToken.OP_MOD, yytext()); }


    /* =========================
       DELIMITADORES
       ========================= */

    "{"     { return t(TipoToken.LLAVE_ABRE, yytext()); }
    "}"     { return t(TipoToken.LLAVE_CIERRA, yytext()); }
    "("     { return t(TipoToken.PARENT_ABRE, yytext()); }
    ")"     { return t(TipoToken.PARENT_CIERRA, yytext()); }
    ";"     { return t(TipoToken.FIN_SENTENCIA, yytext()); }
    ","     { return t(TipoToken.COMA, yytext()); }
    ":"     { return t(TipoToken.DOS_PUNTOS, yytext()); }


    /* =========================
       LITERALES
       IMPORTANTE: número real antes que número entero.
       ========================= */

    {NumeroReal}      { return t(TipoToken.NUMERO_REAL, yytext()); }
    {NumeroEntero}    { return t(TipoToken.NUMERO_ENTERO, yytext()); }
    {Cadena}          { return t(TipoToken.VALOR_CADENA, yytext()); }
    {Caracter}        { return t(TipoToken.VALOR_CARACTER, yytext()); }


    /* =========================
       IDENTIFICADORES
       Debe ir después de palabras reservadas.
       ========================= */

    {Identificador}   { return t(TipoToken.IDENTIFICADOR, yytext()); }


    /* =========================
       FIN DE ARCHIVO Y ERROR GENERAL
       ========================= */

    <<EOF>>           { return t(TipoToken.EOF, "EOF"); }

    .                 { return t(TipoToken.ERROR, yytext()); }
}

/* =========================
   COMENTARIOS DE BLOQUE
   ========================= */

<COMENTARIO_BLOQUE> {
    "*/"              { yybegin(YYINITIAL); }
    [^]               { /* Ignorar contenido del comentario */ }
}
