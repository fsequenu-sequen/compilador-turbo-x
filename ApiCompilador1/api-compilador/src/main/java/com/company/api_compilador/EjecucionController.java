package com.company.api_compilador;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*")
@RestController
public class EjecucionController {

    @PostMapping("/api/ejecutar")
    public ResultadoEjecucion ejecutar(@RequestBody SolicitudEjecucion solicitud) {
        String codigo = solicitud != null && solicitud.getCodigo() != null
                ? solicitud.getCodigo()
                : "";

        String entradas = solicitud != null && solicitud.getEntradas() != null
                ? solicitud.getEntradas()
                : "";

        InterpretadorTurboX interpretador = new InterpretadorTurboX();
        return interpretador.ejecutar(codigo, entradas);
    }
}
