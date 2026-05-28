package com.company.api_compilador;

import java.io.StringReader;

/**
 * Clase simple para probar el parser sin usar la interfaz web.
 * Ejecuta esta clase como main si quieres comprobar rápidamente JCUP.
 */
public class PruebaParserLocal {

    public static void main(String[] args) {

        String codigo = """
                PROGRAMA SistemaNotas
                INICIO

                    ENTERO edad = 18;
                    REAL promedio = 87.50;
                    CADENA nombre = "Carlos";
                    CARACTER seccion = 'A';
                    LOGICO activo = VERDADERO;

                    IMPRIMIR("Inicio del programa");
                    IMPRIMIR(nombre);

                    SI (edad >= 18 Y activo == VERDADERO) ENTONCES {
                        IMPRIMIR("Estudiante mayor de edad activo");
                    } SINO {
                        IMPRIMIR("Estudiante menor o inactivo");
                    }

                    MIENTRAS (edad < 25) HACER {
                        edad = edad + 1;
                        IMPRIMIR(edad);
                    }

                    EVALUAR (edad) {
                        CASO 18:
                            IMPRIMIR("Tiene 18 años");
                            PARAR;

                        CASO 25:
                            IMPRIMIR("Tiene 25 años");
                            PARAR;

                        OTRO:
                            IMPRIMIR("Edad diferente");
                            PARAR;
                    }

                    GRAFICAR f(x) = x ^ 2 + 3;

                FIN
                """;

        try {
            LexerCup lexer = new LexerCup(new StringReader(codigo));
            ParserCup parser = new ParserCup(lexer);

            parser.parse();

            String reporte = parser.reporte.toString().trim();

            if (reporte.isEmpty()) {
                System.out.println("ANÁLISIS SINTÁCTICO CORRECTO");
            } else {
                System.out.println("ANÁLISIS SINTÁCTICO CON ERRORES:");
                System.out.println(reporte);
            }

        } catch (Exception e) {
            System.err.println("ERROR AL EJECUTAR EL PARSER:");
            e.printStackTrace();
        }
    }
}
