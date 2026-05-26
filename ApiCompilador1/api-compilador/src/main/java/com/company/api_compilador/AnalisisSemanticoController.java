package com.company.api_compilador;

import java.util.Map;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class AnalisisSemanticoController {

    @PostMapping("/semantico")
    public ResultadoSemantico analizarSemantica(@RequestBody Map<String, String> request) {
        String codigo = request.getOrDefault("codigo", "");
        return new AnalizadorSemantico().analizar(codigo);
    }
}
