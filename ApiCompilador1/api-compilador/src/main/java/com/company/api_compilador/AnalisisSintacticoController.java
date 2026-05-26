package com.company.api_compilador;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador para ejecutar el análisis sintáctico con JCUP.
 */
@RestController
@CrossOrigin(origins = "*")
public class AnalisisSintacticoController {

    @PostMapping("/api/sintactico")
    public ResultadoSintactico analizarSintaxis(@RequestBody PeticionSintactica peticion) {

        List<String> errores = new ArrayList<>();

        try {
            String codigo = peticion.getCodigo();

            if (codigo == null || codigo.trim().isEmpty()) {
                errores.add("No se recibio codigo fuente para analizar.");
                return new ResultadoSintactico(false, "No hay codigo para analizar.", errores);
            }

            LexerCup lexer = new LexerCup(new StringReader(codigo));
            ParserCup parser = new ParserCup(lexer);

            parser.parse();

            String reporte = parser.reporte.toString().trim();

            if (!reporte.isEmpty()) {
                errores.addAll(Arrays.asList(reporte.split("\\n")));
            }

            if (errores.isEmpty()) {
                return new ResultadoSintactico(
                        true,
                        "Analisis sintactico finalizado correctamente. No se detectaron errores.",
                        errores
                );
            }

            return new ResultadoSintactico(
                    false,
                    "Analisis sintactico finalizado con errores.",
                    errores
            );

        } catch (Exception e) {

            String mensaje = e.getMessage();

            if (mensaje == null || mensaje.isBlank()) {
                mensaje = "Error sintactico no recuperable.";
            }

            errores.add(mensaje);

            return new ResultadoSintactico(
                    false,
                    "Analisis sintactico finalizado con errores.",
                    errores
            );
        }
    }
}

/**
 * Clase auxiliar para recibir el JSON:
 * {
 *   "codigo": "PROGRAMA ..."
 * }
 */
class PeticionSintactica {

    private String codigo;

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
}
