/** Chimaltenango 30 de mayo 2026
Proyecto Final Compiladores
Integrantes: 
1990-23-4406	Christopher Obryan Mazariegos Crúz
1990-23-17188	Luis Miguel Vaquiax Camey
1990-23-10442	Keyner Alejandro Rivera Axpuac
1990-23-22934	Freyder José Sequén Urlao
*/
package com.company.api_compilador.sintactico;

import java.io.File;

/**
 * Genera los archivos necesarios para el análisis sintáctico con JCUP.
 *
 * Este generador crea:
 * 1. ParserCup.java
 * 2. SimbolosCup.java
 * 3. LexerCup.java
 *
 * IMPORTANTE:
 * Ejecutar esta clase después de agregar las dependencias de JCUP en pom.xml.
 */
public class GeneradorSintactico {

    /** Punto de entrada manual para regenerar parser y lexer sintáctico. */
    public static void main(String[] args) {
        String carpetaBase = "src" + File.separator + "main" + File.separator + "java"
                + File.separator + "com" + File.separator + "company"
                + File.separator + "api_compilador" + File.separator + "sintactico";

        String rutaCup = carpetaBase + File.separator + "parser.cup";
        String rutaLexerCup = carpetaBase + File.separator + "LexerCup.flex";

        generarParser(rutaCup, carpetaBase);
        generarLexerCup(rutaLexerCup);
    }

    /**
     * Genera ParserCup.java y SimbolosCup.java a partir de parser.cup.
     * Usa reflexión para no obligar a tener java-cup completo durante el build normal.
     */
    private static void generarParser(String rutaCup, String destino) {
        try {
            File archivoCup = new File(rutaCup);

            if (!archivoCup.exists()) {
                System.err.println("ERROR: No se encontró parser.cup en: " + archivoCup.getAbsolutePath());
                return;
            }

            System.out.println("SISTEMA: Generando parser con JCUP...");

            String[] argumentosCup = {
                    "-parser", "ParserCup",
                    "-symbols", "SimbolosCup",
                    "-destdir", destino,
                    rutaCup
            };

            // Se usa reflexión para no obligar al backend a cargar java-cup completo al compilar.
            // El backend solo necesita java-cup-runtime para ejecutar el parser ya generado.
            // Si se desea regenerar el parser, debe agregarse java-cup completo temporalmente.
            Class<?> cupMain = Class.forName("java_cup.Main");
            java.lang.reflect.Method metodoMain = cupMain.getMethod("main", String[].class);
            metodoMain.invoke(null, (Object) argumentosCup);

            System.out.println("SISTEMA: ParserCup.java y SimbolosCup.java generados correctamente.");

        } catch (Exception e) {
            System.err.println("ERROR al generar parser con JCUP: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /** Genera LexerCup.java a partir de LexerCup.flex mediante JFlex. */
    private static void generarLexerCup(String rutaLexerCup) {
        try {
            File archivoFlex = new File(rutaLexerCup);

            if (!archivoFlex.exists()) {
                System.err.println("ERROR: No se encontró LexerCup.flex en: " + archivoFlex.getAbsolutePath());
                return;
            }

            System.out.println("SISTEMA: Generando LexerCup.java con JFlex...");

            String[] argumentosFlex = {rutaLexerCup};
            jflex.Main.generate(argumentosFlex);

            System.out.println("SISTEMA: LexerCup.java generado correctamente.");

        } catch (Exception e) {
            System.err.println("ERROR al generar LexerCup con JFlex: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
