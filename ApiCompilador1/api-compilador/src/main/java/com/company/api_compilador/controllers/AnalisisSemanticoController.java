/** Chimaltenango 30 de mayo 2026
Proyecto Final Compiladores
Integrantes: 
1990-23-4406	Christopher Obryan Mazariegos Crúz
1990-23-17188	Luis Miguel Vaquiax Camey
1990-23-10442	Keyner Alejandro Rivera Axpuac
1990-23-22934	Freyder José Sequén Urlao
*/
package com.company.api_compilador.controllers;

import com.company.api_compilador.semantico.AnalizadorSemantico;
import com.company.api_compilador.semantico.ResultadoSemantico;
import java.util.Map;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST encargado del análisis semántico.
 *
 * Esta fase revisa reglas de significado: variables declaradas, tipos
 * compatibles, uso correcto de expresiones, condiciones lógicas, funciones
 * matemáticas y errores como división o módulo entre cero.
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class AnalisisSemanticoController {

    /**
     * Endpoint POST /api/semantico.
     * Recibe el código fuente y delega la revisión a AnalizadorSemantico.
     */
    @PostMapping("/semantico")
    public ResultadoSemantico analizarSemantica(@RequestBody Map<String, String> request) {
        String codigo = request.getOrDefault("codigo", "");
        return new AnalizadorSemantico().analizar(codigo);
    }
}
