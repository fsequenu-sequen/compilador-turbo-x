package com.company.api_compilador.pruebas;

import com.company.api_compilador.semantico.AnalizadorSemantico;
import com.company.api_compilador.semantico.ErrorSemantico;
import com.company.api_compilador.semantico.ResultadoSemantico;
import com.company.api_compilador.semantico.Simbolo;
public class PruebaSemanticaLocal {

    public static void main(String[] args) {
        String codigo = """
                PROGRAMA Demo
                INICIO
                    ENTERO edad = 18;
                    REAL total = 10 + 5.5;
                    CADENA nombre = "Carlos";
                    LOGICO activo = VERDADERO;

                    edad = total;
                    IMPRIMIR(nombre);
                    SI (edad >= 18 Y activo) ENTONCES {
                        IMPRIMIR("Mayor de edad");
                    }

                    ENTERO edad = 20;
                    IMPRIMIR(apellido);
                FIN
                """;

        AnalizadorSemantico analizador = new AnalizadorSemantico();
        ResultadoSemantico resultado = analizador.analizar(codigo);

        System.out.println(resultado.getMensaje());

        for (ErrorSemantico error : resultado.getErrores()) {
            System.out.println("Linea " + error.getLinea() + " | " + error.getLexema() + " | " + error.getDescripcion());
        }

        System.out.println("\nTabla de símbolos:");
        for (Simbolo simbolo : resultado.getTablaSimbolos()) {
            System.out.println(simbolo.getNombre() + " | " + simbolo.getTipo() + " | " + simbolo.getCategoria()
                    + " | linea " + simbolo.getLineaDeclaracion() + " | inicializado=" + simbolo.isInicializado());
        }
    }
}
