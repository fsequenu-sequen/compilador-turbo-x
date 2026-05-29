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

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class GraficaController {

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
