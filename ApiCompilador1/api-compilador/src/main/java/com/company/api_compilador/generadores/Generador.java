/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.company.api_compilador.generadores;
import java.io.File;
/**
 *
 * @author ObryanMazariegos
 */
public class Generador {
    
    public static void main(String[] args) {
        // 1. Especificamos la ruta de tu archivo .flex
        // En proyectos Maven, la ruta suele empezar desde "src/main/java/..."
        //String rutaFlex = "src/main/java/com/tu/paquete/analizador.flex"; 
        String rutaFlex = "src" + File.separator + "main" + File.separator + "java"
                + File.separator + "com" + File.separator + "company"
                + File.separator + "api_compilador" + File.separator + "lexico"
                + File.separator + "Lexer.flex";
        generarLexer(rutaFlex);
    }

    public static void generarLexer(String ruta) {
        File archivo = new File(ruta);
        
        if (archivo.exists()) {
            System.out.println("SISTEMA: Localizando archivo .flex...");
            
            // CAMBIO AQUÍ: JFlex espera un arreglo de Strings con las rutas
            String[] argumentos = {ruta}; 
            
            try {
                // Ahora le pasamos el arreglo 'argumentos'
                jflex.Main.generate(argumentos); 
                System.out.println("SISTEMA: AnalizadorLexico.java generado con éxito.");
            } catch (Exception e) {
                System.err.println("ERROR al generar: " + e.getMessage());
            }
            
        } else {
            System.err.println("ERROR: No se encontró el archivo en: " + archivo.getAbsolutePath());
        }
    }
    
}
