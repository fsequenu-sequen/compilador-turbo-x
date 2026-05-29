/** Chimaltenango 30 de mayo 2026
Proyecto Final Compiladores
Integrantes: 
1990-23-4406	Christopher Obryan Mazariegos Crúz
1990-23-17188	Luis Miguel Vaquiax Camey
1990-23-10442	Keyner Alejandro Rivera Axpuac
1990-23-22934	Freyder José Sequén Urlao
*/
package com.company.api_compilador.controllers;

import com.company.api_compilador.lexico.AnalizadorLexico;
import com.company.api_compilador.lexico.TipoToken;
import com.company.api_compilador.lexico.Token;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador REST encargado del análisis léxico.
 *
 * Recibe código fuente desde la interfaz web, lo envía al analizador léxico
 * generado por JFlex y devuelve la lista de tokens encontrados. Cada token
 * incluye tipo, lexema, línea y columna.
 */
@RestController
@CrossOrigin(origins = "*") // Permite que el frontend web consuma esta API sin bloqueo CORS.
public class CompiladorController {

    /**
     * Endpoint POST /api/analizar.
     *
     * Flujo general:
     * 1. Recibe un JSON con el campo codigo.
     * 2. Crea un StringReader para que el lexer lea el texto como flujo.
     * 3. Extrae tokens uno por uno hasta encontrar EOF.
     * 4. Devuelve la lista para que Spring Boot la convierta a JSON.
     */
    @PostMapping("/api/analizar")
    public List<Token> analizarCodigo(@RequestBody PeticionCodigo peticion) {
        List<Token> listaTokens = new ArrayList<>();
        
        try {
            //Instanciamos el analizador léxico pasándole el código recibido
            AnalizadorLexico lexer = new AnalizadorLexico(new StringReader(peticion.getCodigo()));
            
            while (true) {
                Token token = lexer.proximoToken(); //Llamamos a la función que definiste en tu Lexer.flex
                
                if (token.getTipo() == TipoToken.EOF) { //Si llega al final del archivo, termina
                    break;
                }
                
                listaTokens.add(token); //Agregamos el token a la lista
            }
        } catch (Exception e) {
            System.out.println("Error al analizar: " + e.getMessage());
        }
        
        //Spring Boot convierte automáticamente esta lista a formato JSON para la web
        return listaTokens; 
    }
}

/**
 * DTO auxiliar para deserializar la petición JSON del análisis léxico.
 *
 * Ejemplo esperado desde el frontend:
 * {
 *   "codigo": "PROGRAMA Demo ... FIN"
 * }
 */
class PeticionCodigo {
    // Código fuente escrito por el usuario en el editor web.
    private String codigo;

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
}