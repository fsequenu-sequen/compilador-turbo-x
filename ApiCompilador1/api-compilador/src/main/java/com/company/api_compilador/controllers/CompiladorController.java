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

@RestController
@CrossOrigin(origins = "*") // Esto permite que VS Code (o cualquier web) se conecte sin bloqueos de seguridad
public class CompiladorController {

    //Este método recibe solicitudes POST en la ruta /api/analizar
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

//Clase auxiliar para recibir el JSON con el código
class PeticionCodigo {
    private String codigo;

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
}