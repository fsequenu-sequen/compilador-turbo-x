/** Chimaltenango 30 de mayo 2026
Proyecto Final Compiladores
Integrantes: 
1990-23-4406	Christopher Obryan Mazariegos Crúz
1990-23-17188	Luis Miguel Vaquiax Camey
1990-23-10442	Keyner Alejandro Rivera Axpuac
1990-23-22934	Freyder José Sequén Urlao
*/
package com.company.api_compilador.semantico;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Estructura encargada de administrar la tabla de símbolos.
 *
 * Usa LinkedHashMap para conservar el orden de declaración y permitir búsquedas
 * rápidas por nombre de variable.
 */
public class TablaSimbolos {

    // Mapa principal: clave = nombre de variable, valor = información del símbolo.
    private final Map<String, Simbolo> simbolos = new LinkedHashMap<>();

    /**
     * Registra una nueva variable en la tabla.
     * Devuelve false si ya existía una variable con el mismo nombre.
     */
    public boolean declarar(Simbolo simbolo) {
        if (simbolos.containsKey(simbolo.getNombre())) {
            return false;
        }
        simbolos.put(simbolo.getNombre(), simbolo);
        return true;
    }

    /** Verifica si un nombre ya fue declarado. */
    public boolean existe(String nombre) {
        return simbolos.containsKey(nombre);
    }

    /** Obtiene el símbolo asociado a un nombre de variable. */
    public Simbolo obtener(String nombre) {
        return simbolos.get(nombre);
    }

    /** Marca una variable como inicializada después de asignarle o leerle un valor. */
    public void marcarInicializado(String nombre) {
        Simbolo simbolo = simbolos.get(nombre);
        if (simbolo != null) {
            simbolo.setInicializado(true);
        }
    }

    /** Devuelve la tabla como lista para enviarla al frontend en formato JSON. */
    public List<Simbolo> listar() {
        return new ArrayList<>(simbolos.values());
    }
}

