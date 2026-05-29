/** Chimaltenango 30 de mayo 2026
Proyecto Final Compiladores
Integrantes: 
1990-23-4406	Christopher Obryan Mazariegos Crúz
1990-23-17188	Luis Miguel Vaquiax Camey
1990-23-10442	Keyner Alejandro Rivera Axpuac
1990-23-22934	Freyder José Sequén Urlao
*/
package com.company.api_compilador.controllers;

import com.company.api_compilador.ejecucion.InterpretadorTurboX;
import com.company.api_compilador.ejecucion.ResultadoEjecucion;
import com.company.api_compilador.ejecucion.SolicitudEjecucion;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST encargado de ejecutar programas Turbo X.
 *
 * Esta fase no solo valida, sino que interpreta instrucciones como
 * declaraciones, asignaciones, IMPRIMIR, LEER, SI, MIENTRAS y EVALUAR.
 */
@CrossOrigin(origins = "*")
@RestController
public class EjecucionController {

    /**
     * Endpoint POST /api/ejecutar.
     *
     * Recibe código y entradas simuladas para instrucciones LEER. Se validan
     * valores nulos para evitar errores si el frontend envía una solicitud
     * incompleta.
     */
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
