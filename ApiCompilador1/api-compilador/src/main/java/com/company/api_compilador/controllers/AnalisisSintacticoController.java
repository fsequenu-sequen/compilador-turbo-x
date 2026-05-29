package com.company.api_compilador.controllers;

import com.company.api_compilador.sintactico.LexerCup;
import com.company.api_compilador.sintactico.ParserCup;
import com.company.api_compilador.sintactico.ResultadoSintactico;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class AnalisisSintacticoController {

    @PostMapping("/sintactico")
    public ResultadoSintactico analizarSintaxis(@RequestBody Map<String, String> request) {
        String codigo = request.getOrDefault("codigo", "");

        List<String> diagnosticosPrevios = diagnosticarSintaxisBasica(codigo);

        if (!diagnosticosPrevios.isEmpty()) {
            return new ResultadoSintactico(
                    false,
                    "Se detectaron errores sintácticos antes de ejecutar JCUP.",
                    diagnosticosPrevios
            );
        }

        ParserCup parser = null;

        try {
            LexerCup lexer = new LexerCup(new StringReader(codigo));
            parser = new ParserCup(lexer);

            parser.parse();

            List<String> errores = extraerErrores(parser.reporte.toString());

            if (!errores.isEmpty()) {
                return new ResultadoSintactico(
                        false,
                        "Se detectaron errores sintácticos.",
                        errores
                );
            }

            return new ResultadoSintactico(
                    true,
                    "El código cumple con la gramática definida en JCUP.",
                    new ArrayList<>()
            );

        } catch (Exception ex) {
            List<String> errores = new ArrayList<>();

            if (parser != null && parser.reporte != null && parser.reporte.length() > 0) {
                errores.addAll(extraerErrores(parser.reporte.toString()));
            }

            if (errores.isEmpty()) {
                errores.add("No se pudo completar el análisis sintáctico. Revisa la línea indicada por el parser y también la línea anterior.");
                errores.add("Detalle técnico: " + ex.getMessage());
            }

            return new ResultadoSintactico(
                    false,
                    "Se detectaron errores sintácticos.",
                    errores
            );
        }
    }

    private List<String> extraerErrores(String reporte) {
        List<String> errores = new ArrayList<>();

        if (reporte == null || reporte.trim().isEmpty()) {
            return errores;
        }

        String[] lineas = reporte.split("\\R");

        for (String linea : lineas) {
            if (!linea.trim().isEmpty()) {
                errores.add(linea.trim());
            }
        }

        return errores;
    }

    /*
     * Diagnóstico previo para errores frecuentes.
     * Esto ayuda a evitar que JCUP reporte el error en una línea en blanco o en la línea siguiente.
     */
    private List<String> diagnosticarSintaxisBasica(String codigo) {
        List<String> errores = new ArrayList<>();

        if (codigo == null || codigo.trim().isEmpty()) {
            errores.add("Línea 1: no hay código fuente para analizar.");
            return errores;
        }

        String sinComentariosBloque = codigo.replaceAll("(?s)/\\*.*?\\*/", "");
        String[] lineas = sinComentariosBloque.split("\\R");

        int balanceParentesis = 0;
        int balanceLlaves = 0;

        for (int i = 0; i < lineas.length; i++) {
            int numeroLinea = i + 1;
            String linea = quitarComentarioLinea(lineas[i]).trim();

            if (linea.isEmpty()) {
                continue;
            }

            balanceParentesis += contar(linea, '(');
            balanceParentesis -= contar(linea, ')');
            balanceLlaves += contar(linea, '{');
            balanceLlaves -= contar(linea, '}');

            if (requierePuntoYComa(linea) && !linea.endsWith(";")) {
                errores.add("Línea " + numeroLinea + ": posible falta de punto y coma ';' al final de la instrucción: " + linea);
                continue;
            }

            if (linea.startsWith("SI") && !linea.contains("ENTONCES")) {
                errores.add("Línea " + numeroLinea + ": la estructura SI debe incluir la palabra ENTONCES.");
            }

            if (linea.startsWith("MIENTRAS") && !linea.contains("HACER")) {
                errores.add("Línea " + numeroLinea + ": la estructura MIENTRAS debe incluir la palabra HACER.");
            }

            if (linea.startsWith("CASO") && !linea.endsWith(":")) {
                errores.add("Línea " + numeroLinea + ": la instrucción CASO debe terminar con dos puntos ':'.");
            }
        }

        if (balanceParentesis > 0) {
            errores.add("Falta cerrar uno o más paréntesis ')'.");
        } else if (balanceParentesis < 0) {
            errores.add("Hay paréntesis de cierre ')' sin apertura correspondiente.");
        }

        if (balanceLlaves > 0) {
            errores.add("Falta cerrar una o más llaves '}'.");
        } else if (balanceLlaves < 0) {
            errores.add("Hay llaves de cierre '}' sin apertura correspondiente.");
        }

        return errores;
    }

    private boolean requierePuntoYComa(String linea) {
        String l = linea.trim();

        if (l.isEmpty()) {
            return false;
        }

        if (l.equals("INICIO")
                || l.equals("FIN")
                || l.equals("{")
                || l.equals("}")
                || l.equals("} SINO {")
                || l.equals("SINO")
                || l.equals("SINO {")
                || l.equals("OTRO:")
                || l.startsWith("PROGRAMA ")
                || l.endsWith("{")
                || l.endsWith("}")
                || l.endsWith(":")) {
            return false;
        }

        return l.matches("^(ENTERO|REAL|CADENA|CARACTER|LOGICO)\\s+.*")
                || l.matches("^[a-zA-Z_][a-zA-Z0-9_]*\\s*=.*")
                || l.startsWith("IMPRIMIR")
                || l.startsWith("LEER")
                || l.startsWith("PARAR")
                || l.startsWith("GRAFICAR");
    }

    private int contar(String texto, char buscado) {
        int total = 0;

        for (int i = 0; i < texto.length(); i++) {
            if (texto.charAt(i) == buscado) {
                total++;
            }
        }

        return total;
    }

    private String quitarComentarioLinea(String linea) {
        boolean dentroCadena = false;

        for (int i = 0; i < linea.length() - 1; i++) {
            char actual = linea.charAt(i);

            if (actual == '"') {
                dentroCadena = !dentroCadena;
            }

            if (!dentroCadena && actual == '/' && linea.charAt(i + 1) == '/') {
                return linea.substring(0, i);
            }
        }

        return linea;
    }
}
