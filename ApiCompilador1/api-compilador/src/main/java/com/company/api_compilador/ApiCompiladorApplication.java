/** Chimaltenango 30 de mayo 2026
Proyecto Final Compiladores
Integrantes: 
1990-23-4406	Christopher Obryan Mazariegos Crúz
1990-23-17188	Luis Miguel Vaquiax Camey
1990-23-10442	Keyner Alejandro Rivera Axpuac
1990-23-22934	Freyder José Sequén Urlao
*/
package com.company.api_compilador;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal del backend del Compilador Turbo X.
 *
 * La anotación @SpringBootApplication indica a Spring Boot que debe iniciar
 * la aplicación, configurar automáticamente el servidor web y escanear todos
 * los subpaquetes que cuelgan de com.company.api_compilador.
 *
 * Por eso los paquetes controllers, lexico, sintactico, semantico, grafica
 * y ejecucion pueden ser detectados sin configuración adicional.
 */
@SpringBootApplication
public class ApiCompiladorApplication {

    /**
     * Punto de entrada del programa Java.
     *
     * Cuando se ejecuta el proyecto, este método levanta el servidor embebido
     * de Spring Boot y deja disponibles los endpoints REST utilizados por la
     * interfaz web del compilador.
     */
    public static void main(String[] args) {
        SpringApplication.run(ApiCompiladorApplication.class, args);
    }

}
