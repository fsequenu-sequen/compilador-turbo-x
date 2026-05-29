/** Chimaltenango 30 de mayo 2026
Proyecto Final Compiladores
Integrantes: 
1990-23-4406	Christopher Obryan Mazariegos Crúz
1990-23-17188	Luis Miguel Vaquiax Camey
1990-23-10442	Keyner Alejandro Rivera Axpuac
1990-23-22934	Freyder José Sequén Urlao
*/
package com.company.api_compilador.controllers;

import com.company.api_compilador.grafica.ResultadoGrafica;
import com.company.api_compilador.sintactico.LexerCup;
import com.company.api_compilador.sintactico.ParserCup;
import java.io.StringReader;
import java.util.Map;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST encargado del módulo de gráficas.
 *
 * Usa el parser JCUP para detectar instrucciones del tipo:
 * GRAFICAR f(x) = expresion;
 * y devuelve la información necesaria para que el frontend abra la gráfica.
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class GraficaController {

    /**
     * Endpoint POST /api/grafica.
     *
     * Ejecuta el parser. Si encuentra una instrucción GRAFICAR válida, toma
     * los datos almacenados por ParserCup: función, variable, expresión y URL.
     */
    @PostMapping("/grafica")
    public ResultadoGrafica procesarGrafica(@RequestBody Map<String, String> request) {
        String codigo = request.getOrDefault("codigo", "");

        try {
            LexerCup lexer = new LexerCup(new StringReader(codigo));
            ParserCup parser = new ParserCup(lexer);

            parser.parse();

            if (parser.graficaEncontrada) {
                return new ResultadoGrafica(
                        true,
                        "Instrucción GRAFICAR procesada correctamente desde JCUP.",
                        true,
                        parser.funcionGrafica,
                        parser.variableGrafica,
                        parser.expresionGrafica,
                        parser.urlGrafica
                );
            }

            return new ResultadoGrafica(
                    true,
                    "No se encontró una instrucción GRAFICAR en el programa.",
                    false,
                    "",
                    "",
                    "",
                    ""
            );

        } catch (Exception ex) {
            return new ResultadoGrafica(
                    false,
                    "No se pudo procesar la gráfica porque el parser encontró errores sintácticos.",
                    false,
                    "",
                    "",
                    "",
                    ""
            );
        }
    }
}
