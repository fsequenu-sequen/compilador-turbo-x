/** Chimaltenango 30 de mayo 2026
Proyecto Final Compiladores
Integrantes: 
1990-23-4406	Christopher Obryan Mazariegos Crúz
1990-23-17188	Luis Miguel Vaquiax Camey
1990-23-10442	Keyner Alejandro Rivera Axpuac
1990-23-22934	Freyder José Sequén Urlao
*/
package com.company.api_compilador.generadores;
import java.io.File;

/**
 * Generador del analizador léxico principal mediante JFlex.
 *
 * Esta clase no es parte del flujo normal del backend en ejecución. Se usa
 * manualmente cuando se modifica Lexer.flex y se necesita regenerar
 * AnalizadorLexico.java.
 */
public class Generador {
    
    /** Punto de entrada manual para regenerar el lexer léxico. */
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

    /**
     * Ejecuta JFlex sobre el archivo .flex indicado.
     * Si el archivo existe, genera la clase Java correspondiente.
     */
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
