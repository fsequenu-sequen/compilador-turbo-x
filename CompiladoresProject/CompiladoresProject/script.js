/* =========================================================
   TURBO X COMPILER UI
   Frontend conectado a Spring Boot:
   - /api/analizar
   - /api/sintactico
   - /api/semantico
   ========================================================= */

const API_BASE = "http://localhost:8080";

const codigo = document.getElementById("codigo");
const lineNumbers = document.getElementById("lineNumbers");
const archivoTxt = document.getElementById("archivoTxt");
const btnArchivo = document.getElementById("btnArchivo");
const btnEjemplo = document.getElementById("btnEjemplo");
const btnCompilar = document.getElementById("btnCompilar");
const btnLimpiar = document.getElementById("btnLimpiar");
const btnCopiar = document.getElementById("btnCopiar");
const btnLimpiarConsola = document.getElementById("btnLimpiarConsola");
const fileName = document.getElementById("fileName");

const tablaTokens = document.getElementById("tablaTokens");
const tablaSimbolos = document.getElementById("tablaSimbolos");
const consola = document.getElementById("consola");

const metricTokens = document.getElementById("metricTokens");
const metricLexErrors = document.getElementById("metricLexErrors");
const metricSintaxis = document.getElementById("metricSintaxis");
const metricSemantica = document.getElementById("metricSemantica");

const parserStatus = document.getElementById("parserStatus");
const astOutput = document.getElementById("astOutput");
const semanticErrors = document.getElementById("semanticErrors");
const funcionDetectada = document.getElementById("funcionDetectada");
const urlGrafica = document.getElementById("urlGrafica");
const btnAbrirGrafica = document.getElementById("btnAbrirGrafica");
const serverStatus = document.getElementById("serverStatus");

let ultimaUrlGrafica = "";

/* =========================================================
   NAVEGACIÓN ENTRE PANELES
   ========================================================= */

document.querySelectorAll(".menu-item").forEach((button) => {
    button.addEventListener("click", () => {
        document.querySelectorAll(".menu-item").forEach((item) => item.classList.remove("active"));
        document.querySelectorAll(".workspace-panel").forEach((panel) => panel.classList.remove("active-panel"));

        button.classList.add("active");

        const panelId = button.dataset.panel;
        document.getElementById(panelId).classList.add("active-panel");
    });
});

/* =========================================================
   EDITOR Y ARCHIVOS
   ========================================================= */

function actualizarLineas() {
    const totalLineas = codigo.value.split("\n").length || 1;
    let lineas = "";

    for (let i = 1; i <= totalLineas; i++) {
        lineas += i + "<br>";
    }

    lineNumbers.innerHTML = lineas;
}

codigo.addEventListener("input", actualizarLineas);

codigo.addEventListener("scroll", () => {
    lineNumbers.scrollTop = codigo.scrollTop;
});

btnArchivo.addEventListener("click", () => archivoTxt.click());

archivoTxt.addEventListener("change", (event) => {
    const archivo = event.target.files[0];

    if (!archivo) return;

    const lector = new FileReader();

    lector.onload = (e) => {
        codigo.value = e.target.result.replace(/\r\n/g, "\n");
        fileName.textContent = archivo.name;
        actualizarLineas();
        limpiarResultados();
        log("Archivo cargado correctamente: " + archivo.name);
    };

    lector.readAsText(archivo);
    archivoTxt.value = "";
});

btnEjemplo.addEventListener("click", () => {
    codigo.value = ejemploTurboX();
    fileName.textContent = "ejemplo_turbox.tx";
    actualizarLineas();
    limpiarResultados();
    log("Codigo de ejemplo cargado.");
});

btnLimpiar.addEventListener("click", () => {
    codigo.value = "";
    actualizarLineas();
    limpiarResultados();
    log("Editor limpiado.");
});

btnCopiar.addEventListener("click", async () => {
    try {
        await navigator.clipboard.writeText(codigo.value);
        log("Codigo copiado al portapapeles.");
    } catch (error) {
        log("No se pudo copiar el codigo.", "Error");
    }
});

btnLimpiarConsola.addEventListener("click", () => {
    consola.innerHTML = "";
    log("Consola limpia.");
});

/* =========================================================
   COMPILACIÓN REAL POR FASES
   ========================================================= */

btnCompilar.addEventListener("click", async () => {
    const fuente = codigo.value.trim();

    if (!fuente) {
        log("No hay codigo para compilar.", "Advertencia");
        return;
    }

    limpiarResultados(false);

    btnCompilar.disabled = true;
    btnCompilar.textContent = "Compilando...";

    log("Iniciando compilacion por fases...");

    try {
        serverStatus.textContent = "Conectando...";

        // FASE 1: ANALISIS LEXICO
        log("Ejecutando analisis lexico...");
        const tokens = await analizarLexico(fuente);
        renderTokens(tokens);
        renderResumenLexico(tokens);

        const totalErroresLexicos = contarErroresLexicos(tokens);

        if (totalErroresLexicos > 0) {
            renderSintacticoNoEjecutado("No se ejecuto el analisis sintactico porque existen errores lexicos.");
            renderSemanticoNoEjecutado("No se ejecuto el analisis semantico porque existen errores lexicos.");
            renderGraficaNoEjecutada("No se genero grafica porque existen errores lexicos.");

            serverStatus.textContent = "Conectado";
            log("Analisis lexico finalizado con " + totalErroresLexicos + " error(es).", "Error");
            log("Compilacion detenida en la fase lexica.", "Sistema");
            return;
        }

        log("Analisis lexico completado correctamente.");

        // FASE 2: ANALISIS SINTACTICO
        log("Ejecutando analisis sintactico con JCUP...");
        const resultadoSintactico = await analizarSintactico(fuente);
        renderSintactico(resultadoSintactico);

        if (!resultadoSintactico || !resultadoSintactico.correcto) {
            renderSemanticoNoEjecutado("No se ejecuto el analisis semantico porque existen errores sintacticos.");
            renderGraficaNoEjecutada("No se genero grafica porque existen errores sintacticos.");

            serverStatus.textContent = "Conectado";
            log("Analisis sintactico finalizado con errores.", "Error");
            log("Compilacion detenida en la fase sintactica.", "Sistema");
            return;
        }

        log("Analisis sintactico completado correctamente.");

        // FASE 3: ANALISIS SEMANTICO
        log("Ejecutando analisis semantico...");
        const resultadoSemantico = await analizarSemantico(fuente);
        renderSemantico(resultadoSemantico);

        if (!resultadoSemantico || !resultadoSemantico.correcto) {
            renderGraficaNoEjecutada("No se genero grafica porque existen errores semanticos.");

            const cantidadErrores = resultadoSemantico && resultadoSemantico.errores
                ? resultadoSemantico.errores.length
                : 0;

            serverStatus.textContent = "Conectado";
            log("Analisis semantico finalizado con " + cantidadErrores + " error(es).", "Error");
            log("Compilacion finalizada con errores semanticos.", "Sistema");
            return;
        }

        log("Analisis semantico completado correctamente.");

        // FASE 4: GRAFICAS
        renderGraficaDemo(fuente);

        serverStatus.textContent = "Conectado";
        log("Compilacion finalizada correctamente.");

    } catch (error) {
        serverStatus.textContent = "Error de conexion";
        log("No se pudo conectar con el backend. Verifica que Spring Boot este corriendo en localhost:8080.", "Error");
        log(error.message, "Detalle");

        renderSintacticoNoEjecutado("No se recibio respuesta correcta del backend.");
        renderSemanticoNoEjecutado("No se recibio respuesta correcta del backend.");
        renderGraficaNoEjecutada("No se genero grafica por error de conexion.");

    } finally {
        btnCompilar.disabled = false;
        btnCompilar.textContent = "Compilar código";
    }
});

async function analizarLexico(fuente) {
    const respuesta = await fetch(`${API_BASE}/api/analizar`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ codigo: fuente })
    });

    if (!respuesta.ok) {
        throw new Error("Error HTTP en /api/analizar: " + respuesta.status);
    }

    return await respuesta.json();
}

async function analizarSintactico(fuente) {
    const respuesta = await fetch(`${API_BASE}/api/sintactico`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ codigo: fuente })
    });

    if (!respuesta.ok) {
        throw new Error("Error HTTP en /api/sintactico: " + respuesta.status);
    }

    return await respuesta.json();
}

async function analizarSemantico(fuente) {
    const respuesta = await fetch(`${API_BASE}/api/semantico`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ codigo: fuente })
    });

    if (!respuesta.ok) {
        throw new Error("Error HTTP en /api/semantico: " + respuesta.status);
    }

    return await respuesta.json();
}

/* =========================================================
   RENDER DE RESULTADOS
   ========================================================= */

function renderTokens(tokens) {
    if (!tokens || tokens.length === 0) {
        tablaTokens.innerHTML = '<tr><td colspan="5" class="empty-row">No se reconocieron tokens.</td></tr>';
        return;
    }

    tablaTokens.innerHTML = tokens.map((token, index) => `
        <tr>
            <td>${index + 1}</td>
            <td><span class="token-badge ${esErrorToken(token.tipo) ? "token-error" : ""}">${escapeHtml(token.tipo)}</span></td>
            <td>${escapeHtml(token.lexema)}</td>
            <td>${token.linea}</td>
            <td>${token.columna}</td>
        </tr>
    `).join("");
}

function renderResumenLexico(tokens) {
    const errores = contarErroresLexicos(tokens);

    metricTokens.textContent = tokens ? tokens.length : 0;
    metricLexErrors.textContent = errores;
}

function renderSintactico(resultado) {
    if (!resultado) {
        metricSintaxis.textContent = "Sin datos";
        parserStatus.className = "result-box waiting";
        parserStatus.textContent = "No se recibio respuesta del parser.";
        astOutput.textContent = "AST pendiente...";
        return;
    }

    if (resultado.correcto) {
        metricSintaxis.textContent = "Correcta";
        parserStatus.className = "result-box success-box";
        parserStatus.innerHTML = `
            <strong>Sintaxis correcta.</strong><br><br>
            ${escapeHtml(resultado.mensaje)}
        `;

        astOutput.textContent =
`Programa
 ├── Encabezado PROGRAMA
 ├── Bloque INICIO
 │   ├── Declaraciones
 │   ├── Instrucciones
 │   ├── Estructuras de control
 │   ├── Evaluacion multiple
 │   └── Grafica
 └── FIN`;

    } else {
        metricSintaxis.textContent = "Con errores";
        parserStatus.className = "result-box error-box";

        const errores = resultado.errores || [];
        parserStatus.innerHTML = `
            <strong>Se detectaron errores sintacticos.</strong><br><br>
            <ul>
                ${errores.map((e) => `<li>${escapeHtml(e)}</li>`).join("")}
            </ul>
        `;

        astOutput.textContent = "No se genero AST porque existen errores sintacticos.";
    }
}

function renderSintacticoNoEjecutado(motivo) {
    metricSintaxis.textContent = "No ejecutada";
    parserStatus.className = "result-box waiting";
    parserStatus.innerHTML = `
        <strong>Analisis sintactico no ejecutado.</strong><br><br>
        ${escapeHtml(motivo)}
    `;
    astOutput.textContent = "No se genero AST porque la fase sintactica no fue ejecutada.";
}

function renderSemantico(resultado) {
    const simbolos = resultado && resultado.tablaSimbolos ? resultado.tablaSimbolos : [];
    renderTablaSimbolos(simbolos);

    if (!resultado) {
        metricSemantica.textContent = "Sin datos";
        semanticErrors.className = "result-box waiting";
        semanticErrors.textContent = "No se recibio respuesta del analizador semantico.";
        return;
    }

    if (resultado.correcto) {
        metricSemantica.textContent = "Correcta";
        semanticErrors.className = "result-box success-box";
        semanticErrors.innerHTML = `
            <strong>Analisis semantico correcto.</strong><br><br>
            ${escapeHtml(resultado.mensaje)}
        `;
    } else {
        metricSemantica.textContent = "Con errores";
        semanticErrors.className = "result-box error-box";

        const errores = resultado.errores || [];
        semanticErrors.innerHTML = `
            <strong>${escapeHtml(resultado.mensaje)}</strong><br><br>
            <ul>
                ${errores.map((error) => `
                    <li>
                        Linea ${escapeHtml(error.linea)} -
                        <strong>${escapeHtml(error.lexema)}</strong>:
                        ${escapeHtml(error.descripcion)}
                    </li>
                `).join("")}
            </ul>
        `;
    }
}

function renderTablaSimbolos(simbolos) {
    if (!simbolos || simbolos.length === 0) {
        tablaSimbolos.innerHTML = '<tr><td colspan="4" class="empty-row">No se encontraron simbolos declarados.</td></tr>';
        return;
    }

    tablaSimbolos.innerHTML = simbolos.map((simbolo) => {
        const estado = simbolo.inicializado ? "Inicializada" : "Declarada";
        const valor = `${simbolo.categoria || "VARIABLE"} | linea ${simbolo.lineaDeclaracion}`;

        return `
            <tr>
                <td>${escapeHtml(simbolo.nombre)}</td>
                <td>${escapeHtml(simbolo.tipo)}</td>
                <td>${escapeHtml(valor)}</td>
                <td>${escapeHtml(estado)}</td>
            </tr>
        `;
    }).join("");
}

function renderSemanticoNoEjecutado(motivo) {
    metricSemantica.textContent = "No ejecutada";
    tablaSimbolos.innerHTML = '<tr><td colspan="4" class="empty-row">No se genero tabla de simbolos porque la fase semantica no fue ejecutada.</td></tr>';
    semanticErrors.className = "result-box waiting";
    semanticErrors.innerHTML = `
        <strong>Analisis semantico no ejecutado.</strong><br><br>
        ${escapeHtml(motivo)}
    `;
}

function renderGraficaDemo(fuente) {
    const match = fuente.match(/\bGRAFICAR\s+([a-zA-Z_][a-zA-Z0-9_]*)\s*\(\s*x\s*\)\s*=\s*([^;]+);/i);

    if (!match) {
        funcionDetectada.textContent = "f(x) = pendiente";
        urlGrafica.textContent = "No se encontro una sentencia GRAFICAR.";
        btnAbrirGrafica.disabled = true;
        ultimaUrlGrafica = "";
        return;
    }

    const nombreFuncion = match[1];
    const expresion = match[2].trim();

    funcionDetectada.textContent = `${nombreFuncion}(x) = ${expresion}`;

    const expresionDesmos = encodeURIComponent(`y=${expresion.replace(/\*\*/g, "^")}`);
    ultimaUrlGrafica = `https://www.desmos.com/calculator?expression=${expresionDesmos}`;

    urlGrafica.textContent = ultimaUrlGrafica;
    btnAbrirGrafica.disabled = false;
}

function renderGraficaNoEjecutada(motivo) {
    funcionDetectada.textContent = "f(x) = no ejecutada";
    urlGrafica.textContent = motivo;
    btnAbrirGrafica.disabled = true;
    ultimaUrlGrafica = "";
}

btnAbrirGrafica.addEventListener("click", () => {
    if (ultimaUrlGrafica) {
        window.open(ultimaUrlGrafica, "_blank");
    }
});

/* =========================================================
   UTILIDADES
   ========================================================= */

function esErrorToken(tipo) {
    return tipo && String(tipo).startsWith("ERROR");
}

function contarErroresLexicos(tokens) {
    if (!tokens || !Array.isArray(tokens)) {
        return 0;
    }

    return tokens.filter((token) => esErrorToken(token.tipo)).length;
}

function log(mensaje, tipo = "Sistema") {
    const p = document.createElement("p");
    p.innerHTML = `<span>[${escapeHtml(tipo)}]</span> ${escapeHtml(mensaje)}`;
    consola.appendChild(p);
    consola.scrollTop = consola.scrollHeight;
}

function limpiarResultados(limpiarConsola = true) {
    tablaTokens.innerHTML = '<tr><td colspan="5" class="empty-row">Presiona “Compilar codigo” para visualizar los tokens.</td></tr>';
    tablaSimbolos.innerHTML = '<tr><td colspan="4" class="empty-row">Sin datos semanticos todavia.</td></tr>';

    parserStatus.className = "result-box waiting";
    parserStatus.textContent = "Pendiente de ejecutar JCUP.";

    semanticErrors.className = "result-box waiting";
    semanticErrors.textContent = "Pendiente de validar tipos con Java.";

    astOutput.textContent = "AST pendiente...";
    funcionDetectada.textContent = "f(x) = pendiente";
    urlGrafica.textContent = "No se ha generado una URL.";
    btnAbrirGrafica.disabled = true;
    ultimaUrlGrafica = "";

    metricTokens.textContent = "0";
    metricLexErrors.textContent = "0";
    metricSintaxis.textContent = "Pendiente";
    metricSemantica.textContent = "Pendiente";

    if (limpiarConsola) {
        consola.innerHTML = "";
    }
}

function escapeHtml(value) {
    return String(value)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}

function ejemploTurboX() {
    return `PROGRAMA SistemaNotas
INICIO

    // Declaracion de variables
    ENTERO edad = 18;
    REAL promedio = 87.50;
    CADENA nombre = "Carlos";
    CARACTER seccion = 'A';
    LOGICO activo = VERDADERO;

    IMPRIMIR("Inicio del programa");
    IMPRIMIR(nombre);

    SI (edad >= 18 Y activo == VERDADERO) ENTONCES {
        IMPRIMIR("Estudiante mayor de edad activo");
    } SINO {
        IMPRIMIR("Estudiante menor o inactivo");
    }

    MIENTRAS (edad < 25) HACER {
        edad = edad + 1;
        IMPRIMIR(edad);
    }

    EVALUAR (edad) {
        CASO 18:
            IMPRIMIR("Tiene 18 anos");
            PARAR;

        CASO 25:
            IMPRIMIR("Tiene 25 anos");
            PARAR;

        OTRO:
            IMPRIMIR("Edad diferente");
            PARAR;
    }

    GRAFICAR f(x) = x ^ 2 + 3;

FIN`;
}

/* Inicializacion */
actualizarLineas();
log("Interfaz lista. Backend esperado en http://localhost:8080.");
