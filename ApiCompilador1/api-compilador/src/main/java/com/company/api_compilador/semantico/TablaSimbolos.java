package com.company.api_compilador.semantico;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TablaSimbolos {

    private final Map<String, Simbolo> simbolos = new LinkedHashMap<>();

    public boolean declarar(Simbolo simbolo) {
        if (simbolos.containsKey(simbolo.getNombre())) {
            return false;
        }
        simbolos.put(simbolo.getNombre(), simbolo);
        return true;
    }

    public boolean existe(String nombre) {
        return simbolos.containsKey(nombre);
    }

    public Simbolo obtener(String nombre) {
        return simbolos.get(nombre);
    }

    public void marcarInicializado(String nombre) {
        Simbolo simbolo = simbolos.get(nombre);
        if (simbolo != null) {
            simbolo.setInicializado(true);
        }
    }

    public List<Simbolo> listar() {
        return new ArrayList<>(simbolos.values());
    }
}

