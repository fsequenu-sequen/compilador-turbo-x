/* =========================================================
   TURBO X COMPILER UI
   Frontend conectado a Spring Boot:
   - /api/analizar
   - /api/sintactico
   - /api/semantico
   - /api/grafica

   Este script:
   1. Ejecuta el flujo por fases.
   2. Detiene las fases siguientes si hay errores.
   3. Obtiene la URL oficial de Desmos desde JCUP mediante /api/grafica.
   4. Mantiene la gráfica en Canvas como mejora visual adicional.
   ========================================================= */

const API_BASE = "http://localhost:8080";
const CODIGO_STORAGE_KEY = "turbox_codigo_actual_funcional_guias_v1";

const codigo = document.getElementById("codigo");
const lineNumbers = document.getElementById("lineNumbers");
const archivoTxt = document.getElementById("archivoTxt");
const btnArchivo = document.getElementById("btnArchivo");
const btnEjemplo = document.getElementById("btnEjemplo");
const selectorEjemplos = document.getElementById("selectorEjemplos");
const btnLimpiarResultados = document.getElementById("btnLimpiarResultados");
const btnDescargarReporte = document.getElementById("btnDescargarReporte");
const btnTema = document.getElementById("btnTema");
const btnCompilar = document.getElementById("btnCompilar");
const btnEjecutarCodigo = document.getElementById("btnEjecutarCodigo");
const btnLimpiar = document.getElementById("btnLimpiar");
const btnCopiar = document.getElementById("btnCopiar");
const btnLimpiarConsola = document.getElementById("btnLimpiarConsola");
const fileName = document.getElementById("fileName");
const highlightLayer = document.getElementById("highlightLayer");
const errorLineLayer = document.getElementById("errorLineLayer");

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
const entradaPrograma = document.getElementById("entradaPrograma");
const salidaPrograma = document.getElementById("salidaPrograma");
const terminalWindow = document.getElementById("terminalWindow");
const terminalInputRow = document.getElementById("terminalInputRow");
const terminalInput = document.getElementById("terminalInput");
const tablaVariablesRuntime = document.getElementById("tablaVariablesRuntime");
const btnEjecutarPanel = document.getElementById("btnEjecutarPanel");
const btnLimpiarEjecucion = document.getElementById("btnLimpiarEjecucion");
const btnLimpiarEntradas = document.getElementById("btnLimpiarEntradas");
const editorStats = document.getElementById("editorStats");
const autosaveStatus = document.getElementById("autosaveStatus");

let ultimaUrlGrafica = "";

let ejecucionInteractivaActiva = false;
let codigoEjecucionInteractiva = "";
let entradasInteractivas = [];
let historialEntradasTerminal = [];
let salidaPendienteLeer = [];
let ultimoReporteCompilacion = {
    fecha: "",
    codigo: "",
    tokens: [],
    resultadoSintactico: null,
    resultadoSemantico: null,
    resultadoGrafica: null,
    estadoFinal: "Pendiente"
};

let autosaveTimer = null;
let validacionEnVivoTimer = null;
let lineasConError = new Set();
let lineasConAdvertencia = new Set();

/* =========================================================
   NAVEGACIÓN ENTRE PANELES
   ========================================================= */

document.querySelectorAll(".menu-item").forEach((button) => {
    button.addEventListener("click", () => {
        document.querySelectorAll(".menu-item").forEach((item) => item.classList.remove("active"));
        document.querySelectorAll(".workspace-panel").forEach((panel) => panel.classList.remove("active-panel"));

        button.classList.add("active");

        const panelId = button.dataset.panel;
        const panel = document.getElementById(panelId);

        if (panel) {
            panel.classList.add("active-panel");
        }
    });
});




/* =========================================================
   BOTONES RÁPIDOS DEL EDITOR
   Insertan fragmentos de código Turbo X.
   ========================================================= */

function inicializarSnippetsEditor() {
    document.querySelectorAll(".editor-snippet-actions button[data-snippet]").forEach((boton) => {
        boton.addEventListener("click", (event) => {
            event.preventDefault();
            event.stopPropagation();
            insertarSnippetTurboX(boton.dataset.snippet);
        });
    });
}

function insertarSnippetTurboX(tipo) {
    const snippets = {
        programa:
`PROGRAMA NombrePrograma
INICIO

FIN`,

        declaraciones:
`ENTERO numero = 0;
REAL total = 0.0;
CADENA nombre = "Carlos";
CARACTER letra = 'A';
LOGICO activo = VERDADERO;`,

        entero:
`ENTERO numero = 0;`,

        real:
`REAL total = 0.0;`,

        cadena:
`CADENA nombre = "Carlos";`,

        caracter:
`CARACTER letra = 'A';`,

        logico:
`LOGICO activo = VERDADERO;`,

        imprimir:
`IMPRIMIR("Hola mundo");`,

        leer:
`LEER(variable);`,

        si:
`SI (condicion) ENTONCES {
    IMPRIMIR("Condicion verdadera");
}`,

        sino:
`SI (condicion) ENTONCES {
    IMPRIMIR("Condicion verdadera");
} SINO {
    IMPRIMIR("Condicion falsa");
}`,

        mientras:
`MIENTRAS (contador < 5) HACER {
    IMPRIMIR(contador);
    contador = contador + 1;
}`,

        evaluar:
`EVALUAR (opcion) {
    CASO 1:
        IMPRIMIR("Opcion uno");
        PARAR;

    CASO 2:
        IMPRIMIR("Opcion dos");
        PARAR;

    OTRO:
        IMPRIMIR("Otra opcion");
        PARAR;
}`,

        graficar:
`GRAFICAR f(x) = x ^ 2 + x + 2;`
    };

    const texto = snippets[tipo];

    if (!texto || !codigo) {
        return;
    }

    insertarTextoEnEditor(texto);
    log("Fragmento insertado: " + tipo.toUpperCase() + ".");
}

function insertarTextoEnEditor(texto) {
    const inicio = codigo.selectionStart ?? codigo.value.length;
    const fin = codigo.selectionEnd ?? codigo.value.length;

    const antes = codigo.value.substring(0, inicio);
    const despues = codigo.value.substring(fin);

    let textoInsertar = texto;

    // Salto de línea real, no texto visible "\\n".
    if (antes.length > 0 && !antes.endsWith("\n")) {
        textoInsertar = "\n" + textoInsertar;
    }

    if (despues.length > 0 && !textoInsertar.endsWith("\n")) {
        textoInsertar = textoInsertar + "\n";
    }

    codigo.value = antes + textoInsertar + despues;

    const nuevaPosicion = (antes + textoInsertar).length;
    codigo.focus();
    codigo.setSelectionRange(nuevaPosicion, nuevaPosicion);

    actualizarLineas();

    if (typeof guardarCodigoLocal === "function") {
        guardarCodigoLocal();
    }
}

/* =========================================================
   TEMA CLARO / OSCURO
   ========================================================= */

function inicializarTema() {
    const temaGuardado = localStorage.getItem("turbox_tema") || "oscuro";
    aplicarTema(temaGuardado, false);
}

function aplicarTema(tema, registrarLog = true) {
    const esClaro = tema === "claro";

    document.body.classList.toggle("light-theme", esClaro);

    if (btnTema) {
        btnTema.textContent = esClaro ? "☀️ Claro" : "🌙 Oscuro";
        btnTema.title = esClaro ? "Cambiar a tema oscuro" : "Cambiar a tema claro";
    }

    localStorage.setItem("turbox_tema", tema);

    if (registrarLog && consola) {
        log("Tema cambiado a modo " + tema + ".");
    }
}

if (btnTema) {
    btnTema.addEventListener("click", () => {
        const temaActual = document.body.classList.contains("light-theme") ? "claro" : "oscuro";
        const nuevoTema = temaActual === "claro" ? "oscuro" : "claro";

        aplicarTema(nuevoTema);
    });
}

/* =========================================================
   RESALTADO DE SINTAXIS TURBO X
   El resaltado es visual. No altera el análisis del backend.
   Incluye guías de indentación por línea.
   ========================================================= */

function aplicarResaltadoTurboX() {
    if (!highlightLayer || !codigo) {
        return;
    }

    const texto = codigo.value;
    highlightLayer.innerHTML = resaltarCodigoTurboX(texto) + (texto.endsWith("\n") ? " " : "");
    sincronizarScrollEditor();
}

function resaltarCodigoTurboX(texto) {
    return texto
        .split("\n")
        .map((linea) => resaltarLineaTurboXConGuias(linea))
        .join("\n");
}

function resaltarLineaTurboXConGuias(linea) {
    const matchIndentacion = linea.match(/^\s*/);
    const indentacionOriginal = matchIndentacion ? matchIndentacion[0] : "";
    const resto = linea.substring(indentacionOriginal.length);

    let htmlIndentacion = "";
    const indentacionNormalizada = indentacionOriginal.replace(/\t/g, "    ");
    let espacios = indentacionNormalizada.length;

    /*
       Cada bloque de 4 espacios produce una guía vertical.
       La guía solo aparece donde realmente hay indentación.
    */
    while (espacios >= 4) {
        htmlIndentacion += '<span class="syntax-indent-guide">    </span>';
        espacios -= 4;
    }

    if (espacios > 0) {
        htmlIndentacion += escapeHtml(" ".repeat(espacios));
    }

    return htmlIndentacion + resaltarFragmentoTurboX(resto);
}

function resaltarFragmentoTurboX(texto) {
    let resultado = "";
    let i = 0;

    const palabrasReservadas = new Set([
        "PROGRAMA", "INICIO", "FIN",
        "SI", "ENTONCES", "SINO",
        "MIENTRAS", "HACER",
        "EVALUAR", "CASO", "OTRO", "PARAR",
        "IMPRIMIR", "LEER", "GRAFICAR"
    ]);

    const tiposDato = new Set([
        "ENTERO", "REAL", "CADENA", "CARACTER", "LOGICO"
    ]);

    const valoresLogicos = new Set([
        "VERDADERO", "FALSO"
    ]);

    const funcionesMatematicas = new Set([
        "sin", "cos", "tan", "sqrt", "abs", "log", "ln", "exp",
        "SIN", "COS", "TAN", "SQRT", "ABS", "LOG", "LN", "EXP"
    ]);

    while (i < texto.length) {
        const actual = texto[i];
        const siguiente = texto[i + 1] || "";

        // Comentario de línea
        if (actual === "/" && siguiente === "/") {
            resultado += envolverToken(texto.slice(i), "comment");
            break;
        }

        // Comentario de bloque en una misma línea visual
        if (actual === "/" && siguiente === "*") {
            let j = i + 2;

            while (j < texto.length - 1 && !(texto[j] === "*" && texto[j + 1] === "/")) {
                j++;
            }

            j = Math.min(j + 2, texto.length);
            resultado += envolverToken(texto.slice(i, j), "comment");
            i = j;
            continue;
        }

        // Cadenas
        if (actual === '"') {
            let j = i + 1;
            let escapado = false;

            while (j < texto.length) {
                const c = texto[j];

                if (c === '"' && !escapado) {
                    j++;
                    break;
                }

                escapado = c === "\\" && !escapado;

                if (c !== "\\") {
                    escapado = false;
                }

                j++;
            }

            resultado += envolverToken(texto.slice(i, j), "string");
            i = j;
            continue;
        }

        // Caracteres
        if (actual === "'") {
            let j = i + 1;
            let escapado = false;

            while (j < texto.length) {
                const c = texto[j];

                if (c === "'" && !escapado) {
                    j++;
                    break;
                }

                escapado = c === "\\" && !escapado;

                if (c !== "\\") {
                    escapado = false;
                }

                j++;
            }

            resultado += envolverToken(texto.slice(i, j), "char");
            i = j;
            continue;
        }

        // Números
        if (/[0-9]/.test(actual)) {
            let j = i;

            while (j < texto.length && /[0-9.]/.test(texto[j])) {
                j++;
            }

            resultado += envolverToken(texto.slice(i, j), "number");
            i = j;
            continue;
        }

        // Identificadores, reservadas, tipos y funciones
        if (/[A-Za-z_]/.test(actual)) {
            let j = i;

            while (j < texto.length && /[A-Za-z0-9_]/.test(texto[j])) {
                j++;
            }

            const palabra = texto.slice(i, j);
            const mayuscula = palabra.toUpperCase();

            if (palabrasReservadas.has(mayuscula)) {
                resultado += envolverToken(palabra, "keyword");
            } else if (tiposDato.has(mayuscula)) {
                resultado += envolverToken(palabra, "type");
            } else if (valoresLogicos.has(mayuscula)) {
                resultado += envolverToken(palabra, "boolean");
            } else if (funcionesMatematicas.has(palabra) || funcionesMatematicas.has(mayuscula)) {
                resultado += envolverToken(palabra, "function");
            } else {
                resultado += envolverToken(palabra, "identifier");
            }

            i = j;
            continue;
        }

        // Operadores y símbolos
        if (/[+\-*/%^=<>!&|(){}\[\],;:.]/.test(actual)) {
            const dos = texto.slice(i, i + 2);

            if ([">=", "<=", "==", "!=", "&&", "||", "**"].includes(dos)) {
                resultado += envolverToken(dos, "operator");
                i += 2;
            } else {
                resultado += envolverToken(actual, "operator");
                i++;
            }

            continue;
        }

        resultado += escapeHtml(actual);
        i++;
    }

    return resultado;
}

function envolverToken(valor, clase) {
    return `<span class="syntax-${clase}">${escapeHtml(valor)}</span>`;
}

/* =========================================================
   MARCADO DE LÍNEAS CON ERROR Y VALIDACIÓN EN VIVO
   ========================================================= */

function marcarLineasConError(lineas) {
    lineasConError = new Set(
        (lineas || [])
            .map((linea) => Number(linea))
            .filter((linea) => Number.isInteger(linea) && linea > 0)
            .map((linea) => ajustarLineaErrorVisual(linea))
    );

    lineasConAdvertencia.clear();
    actualizarLineas();
}

function limpiarMarcasErroresVisuales() {
    lineasConError.clear();
    lineasConAdvertencia.clear();
    actualizarLineas();
}

function ajustarLineaErrorVisual(linea) {
    const lineas = codigo.value.split("\n");

    if (linea > 1 && linea <= lineas.length && lineas[linea - 1].trim() === "") {
        return linea - 1;
    }

    return linea;
}

function extraerLineasErroresLexicos(tokens) {
    return (tokens || [])
        .filter((token) => esErrorToken(token.tipo))
        .map((token) => token.linea);
}

function extraerLineasErroresSintacticos(resultado) {
    const errores = resultado && resultado.errores ? resultado.errores : [];
    const lineas = [];

    errores.forEach((error) => {
        const texto = String(error);
        const match = texto.match(/l[ií]nea\s+(\d+)/i);

        if (match) {
            lineas.push(Number(match[1]));
        }
    });

    return lineas;
}

function extraerLineasErroresSemanticos(resultado) {
    const errores = resultado && resultado.errores ? resultado.errores : [];

    return errores
        .map((error) => Number(error.linea))
        .filter((linea) => Number.isInteger(linea) && linea > 0);
}

function renderMarcasLineas() {
    if (!errorLineLayer || !codigo) {
        return;
    }

    const style = window.getComputedStyle(codigo);
    let lineHeight = parseFloat(style.lineHeight);
    let paddingTop = parseFloat(style.paddingTop);

    if (!Number.isFinite(lineHeight)) {
        lineHeight = 23.25;
    }

    if (!Number.isFinite(paddingTop)) {
        paddingTop = 16;
    }

    const scrollTop = codigo.scrollTop;
    const fragmentos = [];

    lineasConAdvertencia.forEach((linea) => {
        if (!lineasConError.has(linea)) {
            const top = paddingTop + ((linea - 1) * lineHeight) - scrollTop;
            fragmentos.push(`<div class="live-warning-line" style="top:${top}px;height:${lineHeight}px;"></div>`);
        }
    });

    lineasConError.forEach((linea) => {
        const top = paddingTop + ((linea - 1) * lineHeight) - scrollTop;
        fragmentos.push(`<div class="compile-error-line" style="top:${top}px;height:${lineHeight}px;"></div>`);
    });

    errorLineLayer.innerHTML = fragmentos.join("");
}

function programarValidacionEnVivo() {
    if (validacionEnVivoTimer) {
        clearTimeout(validacionEnVivoTimer);
    }

    validacionEnVivoTimer = setTimeout(() => {
        validarCodigoEnVivo();
    }, 450);
}

function validarCodigoEnVivo() {
    if (!codigo || lineasConError.size > 0) {
        return;
    }

    const lineas = codigo.value.split("\n");
    const advertencias = [];

    lineas.forEach((lineaOriginal, index) => {
        const numeroLinea = index + 1;
        const lineaSinComentario = quitarComentarioLineaCliente(lineaOriginal);
        const linea = lineaSinComentario.trim();

        if (!linea) {
            return;
        }

        if (debeIgnorarseEnValidacionEnVivo(linea)) {
            return;
        }

        if (tieneComillasDoblesImpares(lineaSinComentario)) {
            advertencias.push({
                linea: numeroLinea,
                mensaje: "Posible cadena sin cerrar."
            });
        }

        if (tieneComillasSimplesImpares(lineaSinComentario)) {
            advertencias.push({
                linea: numeroLinea,
                mensaje: "Posible carácter sin cerrar."
            });
        }

        if (posibleFaltaPuntoComa(linea)) {
            advertencias.push({
                linea: numeroLinea,
                mensaje: "Posible falta de punto y coma ';'."
            });
        }

        if (/(\/|%)\s*0+(\.0+)?\b/.test(linea)) {
            advertencias.push({
                linea: numeroLinea,
                mensaje: "Posible división o módulo entre cero."
            });
        }
    });

    lineasConAdvertencia = new Set(advertencias.map((a) => a.linea));
    actualizarLineas();

    if (autosaveStatus) {
        if (advertencias.length > 0) {
            autosaveStatus.textContent = "Advertencias en vivo: " + advertencias.length;
        } else {
            autosaveStatus.textContent = "Sin advertencias en vivo";
        }
    }
}

function debeIgnorarseEnValidacionEnVivo(linea) {
    if (linea === "INICIO" || linea === "FIN") {
        return true;
    }

    if (linea === "{" || linea === "}" || linea === "} SINO {" || linea === "SINO") {
        return true;
    }

    if (linea.startsWith("PROGRAMA ")) {
        return true;
    }

    if (linea.endsWith("{") || linea.endsWith("}") || linea.endsWith(":")) {
        return true;
    }

    if (linea.startsWith("//") || linea.startsWith("/*") || linea.endsWith("*/")) {
        return true;
    }

    return false;
}

function posibleFaltaPuntoComa(linea) {
    if (linea.endsWith(";")) {
        return false;
    }

    // No marcar líneas claramente estructurales.
    if (linea.endsWith("{") || linea.endsWith("}") || linea.endsWith(":")) {
        return false;
    }

    const patronesQueRequierenPuntoComa = [
        /^(ENTERO|REAL|CADENA|CARACTER|LOGICO)\s+[A-Za-z_][A-Za-z0-9_]*(\s*=.+)?$/i,
        /^[A-Za-z_][A-Za-z0-9_]*\s*=.+$/,
        /^IMPRIMIR\s*\(.+\)$/i,
        /^LEER\s*\(.+\)$/i,
        /^PARAR$/i,
        /^GRAFICAR\s+.+$/i
    ];

    return patronesQueRequierenPuntoComa.some((patron) => patron.test(linea));
}

function quitarComentarioLineaCliente(linea) {
    let dentroCadena = false;
    let dentroCaracter = false;

    for (let i = 0; i < linea.length - 1; i++) {
        const actual = linea[i];

        if (actual === '"' && !dentroCaracter) {
            dentroCadena = !dentroCadena;
        }

        if (actual === "'" && !dentroCadena) {
            dentroCaracter = !dentroCaracter;
        }

        if (!dentroCadena && !dentroCaracter && actual === "/" && linea[i + 1] === "/") {
            return linea.substring(0, i);
        }
    }

    return linea;
}

function tieneComillasDoblesImpares(linea) {
    const sinEscapadas = linea.replace(/\\"/g, "");
    const cantidad = (sinEscapadas.match(/"/g) || []).length;
    return cantidad % 2 !== 0;
}

function tieneComillasSimplesImpares(linea) {
    const sinEscapadas = linea.replace(/\\'/g, "");
    const cantidad = (sinEscapadas.match(/'/g) || []).length;
    return cantidad % 2 !== 0;
}



function activarPanel(panelId) {
    document.querySelectorAll(".menu-item").forEach((item) => {
        item.classList.toggle("active", item.dataset.panel === panelId);
    });

    document.querySelectorAll(".workspace-panel").forEach((panel) => {
        panel.classList.toggle("active-panel", panel.id === panelId);
    });
}

/* =========================================================
   EDITOR Y ARCHIVOS
   ========================================================= */


function sincronizarScrollEditor() {
    if (!codigo) {
        return;
    }

    const scrollTop = codigo.scrollTop;
    const scrollLeft = codigo.scrollLeft;

    /*
       Los números de línea deben usar scrollTop, no transform.
       Así se mantienen todos los números disponibles y se mueven junto al textarea.
    */
    if (lineNumbers) {
        lineNumbers.scrollTop = scrollTop;
        lineNumbers.style.transform = "none";
    }

    /*
       La capa de resaltado sí se desplaza con transform porque no es editable.
       Su altura queda automática para que no corte el resto del código.
    */
    if (highlightLayer) {
        highlightLayer.style.transform = `translate(${-scrollLeft}px, ${-scrollTop}px)`;
    }
}


function actualizarLineas() {
    const texto = codigo.value;
    const totalLineas = texto.length === 0 ? 1 : texto.split("\n").length;
    let lineas = "";

    for (let i = 1; i <= totalLineas; i++) {
        let clase = "";

        if (lineasConError.has(i)) {
            clase = "line-number-error";
        } else if (lineasConAdvertencia.has(i)) {
            clase = "line-number-warning";
        }

        /*
           Se genera un span por cada línea, incluso si la línea está vacía.
           Esto mantiene la numeración igual al contenido real del textarea.
        */
        lineas += `<span class="${clase}">${i}</span>`;
    }

    lineNumbers.innerHTML = lineas;
    aplicarResaltadoTurboX();
    sincronizarScrollEditor();
    renderMarcasLineas();
    actualizarEstadisticasEditor();
    programarAutoguardado();
}

codigo.addEventListener("input", () => {
    /*
       Si el usuario edita el código, las marcas de compilaciones anteriores
       ya no son confiables. Se limpian y luego se ejecuta la validación ligera.
    */
    lineasConError.clear();
    lineasConAdvertencia.clear();
    actualizarLineas();
    programarValidacionEnVivo();
});

codigo.addEventListener("scroll", () => {
    sincronizarScrollEditor();
    renderMarcasLineas();
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
    const tipoEjemplo = selectorEjemplos ? selectorEjemplos.value : "correcto";
    codigo.value = obtenerEjemploTurboX(tipoEjemplo);
    fileName.textContent = "ejemplo_" + tipoEjemplo + ".tx";
    actualizarLineas();
    limpiarResultados();
    guardarCodigoLocal();
    log("Ejemplo cargado: " + obtenerNombreEjemplo(tipoEjemplo) + ".");
});

btnLimpiar.addEventListener("click", () => {
    codigo.value = "";
    actualizarLineas();
    limpiarResultados();
    guardarCodigoLocal();
    log("Editor limpiado.");
});

btnCopiar.addEventListener("click", async () => {
    try {
        await navigator.clipboard.writeText(codigo.value);
        log("Código copiado al portapapeles.");
    } catch (error) {
        log("No se pudo copiar el código.", "Error");
    }
});

btnLimpiarConsola.addEventListener("click", () => {
    consola.innerHTML = "";
    log("Consola limpia.");
});


if (btnLimpiarResultados) {
    btnLimpiarResultados.addEventListener("click", () => {
        limpiarResultados(false);
        log("Resultados limpiados. El código del editor se conserva.");
    });
}

if (btnDescargarReporte) {
    btnDescargarReporte.addEventListener("click", () => {
        descargarReporteCompilacion();
    });
}

/* =========================================================
   COMPILACIÓN REAL POR FASES
   ========================================================= */

btnCompilar.addEventListener("click", async () => {
    const fuente = codigo.value.trim();

    ultimoReporteCompilacion = crearReporteBase(fuente);

    if (!fuente) {
        log("No hay código para compilar.", "Advertencia");
        return;
    }

    limpiarResultados(false);
    lineasConError.clear();
    lineasConAdvertencia.clear();
    actualizarLineas();

    btnCompilar.disabled = true;
    btnCompilar.textContent = "Compilando...";

    log("Iniciando compilación por fases...");

    try {
        serverStatus.textContent = "Conectando...";

        /* FASE 1: ANÁLISIS LÉXICO */
        log("Ejecutando análisis léxico...");
        const tokens = await analizarLexico(fuente);

        ultimoReporteCompilacion.tokens = tokens || [];

        renderTokens(tokens);
        renderResumenLexico(tokens);

        const totalErroresLexicos = contarErroresLexicos(tokens);

        if (totalErroresLexicos > 0) {
            marcarLineasConError(extraerLineasErroresLexicos(tokens));
            renderSintacticoNoEjecutado("No se ejecutó el análisis sintáctico porque existen errores léxicos.");
            renderSemanticoNoEjecutado("No se ejecutó el análisis semántico porque existen errores léxicos.");
            renderGraficaNoEjecutada("No se generó gráfica porque existen errores léxicos.");

            serverStatus.textContent = "Conectado";
            log("Análisis léxico finalizado con " + totalErroresLexicos + " error(es).", "Error");
            log("Compilación detenida en la fase léxica.", "Sistema");
            ultimoReporteCompilacion.estadoFinal = "Compilación detenida por errores léxicos.";
            return;
        }

        log("Análisis léxico completado correctamente.");

        /* FASE 2: ANÁLISIS SINTÁCTICO */
        log("Ejecutando análisis sintáctico con JCUP...");
        const resultadoSintactico = await analizarSintactico(fuente);

        ultimoReporteCompilacion.resultadoSintactico = resultadoSintactico;

        renderSintactico(resultadoSintactico);

        if (!resultadoSintactico || !resultadoSintactico.correcto) {
            marcarLineasConError(extraerLineasErroresSintacticos(resultadoSintactico));
            renderSemanticoNoEjecutado("No se ejecutó el análisis semántico porque existen errores sintácticos.");
            renderGraficaNoEjecutada("No se generó gráfica porque existen errores sintácticos.");

            serverStatus.textContent = "Conectado";
            log("Análisis sintáctico finalizado con errores.", "Error");
            log("Compilación detenida en la fase sintáctica.", "Sistema");
            ultimoReporteCompilacion.estadoFinal = "Compilación detenida por errores sintácticos.";
            return;
        }

        log("Análisis sintáctico completado correctamente.");

        /* FASE 3: ANÁLISIS SEMÁNTICO */
        log("Ejecutando análisis semántico...");
        const resultadoSemantico = await analizarSemantico(fuente);

        ultimoReporteCompilacion.resultadoSemantico = resultadoSemantico;

        renderSemantico(resultadoSemantico);

        if (!resultadoSemantico || !resultadoSemantico.correcto) {
            marcarLineasConError(extraerLineasErroresSemanticos(resultadoSemantico));
            renderGraficaNoEjecutada("No se generó gráfica porque existen errores semánticos.");

            const cantidadErrores = resultadoSemantico && resultadoSemantico.errores
                ? resultadoSemantico.errores.length
                : 0;

            serverStatus.textContent = "Conectado";
            log("Análisis semántico finalizado con " + cantidadErrores + " error(es).", "Error");
            log("Compilación finalizada con errores semánticos.", "Sistema");
            ultimoReporteCompilacion.estadoFinal = "Compilación finalizada con errores semánticos.";
            return;
        }

        log("Análisis semántico completado correctamente.");

        /* FASE 4: MÓDULO DE GRÁFICAS CON JCUP + DESMOS */
        log("Procesando módulo de gráficas desde JCUP...");
        const resultadoGrafica = await procesarGraficaConJCUP(fuente);

        ultimoReporteCompilacion.resultadoGrafica = resultadoGrafica;

        renderGraficaDesdeBackend(resultadoGrafica);

        serverStatus.textContent = "Conectado";
        limpiarMarcasErroresVisuales();
        ultimoReporteCompilacion.estadoFinal = "Compilación finalizada correctamente.";
        log("Compilación finalizada correctamente.");

    } catch (error) {
        serverStatus.textContent = "Error de conexión";
        log("No se pudo conectar correctamente con el backend. Verifica que Spring Boot esté corriendo en localhost:8080.", "Error");
        log(error.message, "Detalle");

        renderSintacticoNoEjecutado("No se recibió respuesta correcta del backend.");
        renderSemanticoNoEjecutado("No se recibió respuesta correcta del backend.");
        renderGraficaNoEjecutada("No se generó gráfica por error de conexión.");
        ultimoReporteCompilacion.estadoFinal = "Error de conexión con el backend.";

    } finally {
        btnCompilar.disabled = false;
        btnCompilar.textContent = "Compilar código";
    }
});

/* =========================================================
   PETICIONES AL BACKEND
   ========================================================= */

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

async function procesarGraficaConJCUP(fuente) {
    const respuesta = await fetch(`${API_BASE}/api/grafica`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ codigo: fuente })
    });

    if (!respuesta.ok) {
        throw new Error("Error HTTP en /api/grafica: " + respuesta.status);
    }

    return await respuesta.json();
}

async function ejecutarProgramaBackend(fuente, entradas) {
    const respuesta = await fetch(`${API_BASE}/api/ejecutar`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            codigo: fuente,
            entradas: entradas || ""
        })
    });

    if (!respuesta.ok) {
        throw new Error("Error HTTP en /api/ejecutar: " + respuesta.status);
    }

    return await respuesta.json();
}


/* =========================================================
   RENDER DE TOKENS
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

/* =========================================================
   RENDER SINTÁCTICO
   ========================================================= */

function renderSintactico(resultado) {
    if (!resultado) {
        metricSintaxis.textContent = "Sin datos";
        parserStatus.className = "result-box waiting";
        parserStatus.textContent = "No se recibió respuesta del parser.";
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
 │   ├── Evaluación múltiple
 │   └── Módulo de gráfica
 └── FIN`;

    } else {
        metricSintaxis.textContent = "Con errores";
        parserStatus.className = "result-box error-box";

        const errores = resultado.errores || [];

        parserStatus.innerHTML = `
            <strong>Se detectaron errores sintácticos.</strong><br><br>
            <ul>
                ${errores.map((e) => `<li>${escapeHtml(e)}</li>`).join("")}
            </ul>
        `;

        astOutput.textContent = "No se generó AST porque existen errores sintácticos.";
    }
}

function renderSintacticoNoEjecutado(motivo) {
    metricSintaxis.textContent = "No ejecutada";
    parserStatus.className = "result-box waiting";
    parserStatus.innerHTML = `
        <strong>Análisis sintáctico no ejecutado.</strong><br><br>
        ${escapeHtml(motivo)}
    `;
    astOutput.textContent = "No se generó AST porque la fase sintáctica no fue ejecutada.";
}

/* =========================================================
   RENDER SEMÁNTICO
   ========================================================= */

function renderSemantico(resultado) {
    const simbolos = resultado && resultado.tablaSimbolos ? resultado.tablaSimbolos : [];
    renderTablaSimbolos(simbolos);

    if (!resultado) {
        metricSemantica.textContent = "Sin datos";
        semanticErrors.className = "result-box waiting";
        semanticErrors.textContent = "No se recibió respuesta del analizador semántico.";
        return;
    }

    if (resultado.correcto) {
        metricSemantica.textContent = "Correcta";
        semanticErrors.className = "result-box success-box";
        semanticErrors.innerHTML = `
            <strong>Análisis semántico correcto.</strong><br><br>
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
                        Línea ${escapeHtml(error.linea)} -
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
        tablaSimbolos.innerHTML = '<tr><td colspan="4" class="empty-row">No se encontraron símbolos declarados.</td></tr>';
        return;
    }

    tablaSimbolos.innerHTML = simbolos.map((simbolo) => {
        const estado = simbolo.inicializado ? "Inicializada" : "Declarada";
        const valor = `${simbolo.categoria || "VARIABLE"} | línea ${simbolo.lineaDeclaracion}`;

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
    tablaSimbolos.innerHTML = '<tr><td colspan="4" class="empty-row">No se generó tabla de símbolos porque la fase semántica no fue ejecutada.</td></tr>';
    semanticErrors.className = "result-box waiting";
    semanticErrors.innerHTML = `
        <strong>Análisis semántico no ejecutado.</strong><br><br>
        ${escapeHtml(motivo)}
    `;
}


function asegurarPanelGraficaVisible() {
    const wrapper = document.getElementById("graficaCanvasWrapper");
    const canvas = document.getElementById("graficaCanvas");

    if (wrapper) {
        wrapper.style.display = "block";
        wrapper.classList.add("canvas-visible-card");
    }

    if (canvas) {
        canvas.style.display = "block";
    }
}


/* =========================================================
   MÓDULO DE GRÁFICAS
   URL OFICIAL DESDE JCUP + VISUALIZACIÓN CANVAS
   ========================================================= */

function renderGraficaDesdeBackend(resultado) {
    if (!resultado) {
        renderGraficaNoEjecutada("No se recibió respuesta del módulo de gráficas.");
        return;
    }

    if (!resultado.correcta) {
        renderGraficaNoEjecutada(resultado.mensaje || "No se pudo procesar la gráfica.");
        return;
    }

    if (!resultado.graficaEncontrada) {
        funcionDetectada.textContent = "f(x) = pendiente";
        urlGrafica.textContent = resultado.mensaje || "No se encontró una sentencia GRAFICAR.";
        btnAbrirGrafica.disabled = true;
        ultimaUrlGrafica = "";
        limpiarCanvasGrafica("No se encontró una instrucción GRAFICAR para dibujar.");
        log("No se encontró una instrucción GRAFICAR en el programa.");
        return;
    }

    funcionDetectada.textContent = `${resultado.funcion}(${resultado.variable}) = ${resultado.expresion}`;

    ultimaUrlGrafica = resultado.url || "";
    urlGrafica.textContent = resultado.url || "URL no disponible.";
    btnAbrirGrafica.disabled = !ultimaUrlGrafica;

    asegurarPanelGraficaVisible();
    dibujarGraficaEnCanvas(resultado.expresion);

    log("URL de gráfica generada por JCUP: " + resultado.url);
}

function renderGraficaNoEjecutada(motivo) {
    funcionDetectada.textContent = "f(x) = no ejecutada";
    urlGrafica.textContent = motivo;
    btnAbrirGrafica.disabled = true;
    ultimaUrlGrafica = "";
    asegurarPanelGraficaVisible();
    limpiarCanvasGrafica(motivo);
}

function asegurarCanvasGrafica() {
    let canvas = document.getElementById("graficaCanvas");

    if (canvas) {
        return canvas;
    }

    const contenedorBase = encontrarContenedorGrafica();

    const wrapper = document.createElement("div");
    wrapper.id = "graficaCanvasWrapper";
    wrapper.style.marginTop = "18px";
    wrapper.style.padding = "14px";
    wrapper.style.borderRadius = "14px";
    wrapper.style.background = "rgba(15, 23, 42, 0.65)";
    wrapper.style.border = "1px solid rgba(148, 163, 184, 0.25)";

    const titulo = document.createElement("h3");
    titulo.textContent = "Vista de la gráfica";
    titulo.style.margin = "0 0 10px 0";

    canvas = document.createElement("canvas");
    canvas.id = "graficaCanvas";
    canvas.width = 900;
    canvas.height = 420;
    canvas.style.width = "100%";
    canvas.style.maxWidth = "100%";
    canvas.style.height = "420px";
    canvas.style.display = "block";
    canvas.style.borderRadius = "12px";
    canvas.style.background = "#ffffff";

    const ayuda = document.createElement("p");
    ayuda.id = "graficaAyuda";
    ayuda.style.margin = "10px 0 0 0";
    ayuda.style.fontSize = "13px";
    ayuda.style.opacity = "0.85";
    ayuda.textContent = "Rango utilizado: x de -10 a 10. La escala vertical se ajusta automáticamente.";

    wrapper.appendChild(titulo);
    wrapper.appendChild(canvas);
    wrapper.appendChild(ayuda);

    contenedorBase.appendChild(wrapper);

    return canvas;
}

function encontrarContenedorGrafica() {
    const posiblesIds = [
        "graphPanel",
        "panelGraficas",
        "graficas",
        "grafica",
        "graphicsPanel",
        "panel-graficas"
    ];

    for (const id of posiblesIds) {
        const panel = document.getElementById(id);
        if (panel) {
            return panel;
        }
    }

    if (urlGrafica && urlGrafica.parentElement) {
        return urlGrafica.parentElement;
    }

    return document.body;
}

function limpiarCanvasGrafica(mensaje) {
    const canvas = document.getElementById("graficaCanvas");

    if (!canvas) {
        return;
    }

    asegurarPanelGraficaVisible();

    const contenedor = document.getElementById("graficaCanvasWrapper") || canvas.parentElement;
    const anchoDisponible = contenedor ? contenedor.clientWidth : 1000;
    const cssWidth = Math.max(720, Math.min(anchoDisponible - 36, 1160));
    const cssHeight = 420;

    canvas.style.width = cssWidth + "px";
    canvas.style.height = cssHeight + "px";

    const dpr = window.devicePixelRatio || 1;
    canvas.width = Math.floor(cssWidth * dpr);
    canvas.height = Math.floor(cssHeight * dpr);

    const ctx = canvas.getContext("2d");
    ctx.setTransform(dpr, 0, 0, dpr, 0, 0);

    ctx.clearRect(0, 0, cssWidth, cssHeight);

    const fondo = ctx.createLinearGradient(0, 0, 0, cssHeight);
    fondo.addColorStop(0, "#ffffff");
    fondo.addColorStop(1, "#f8fafc");
    ctx.fillStyle = fondo;
    ctx.fillRect(0, 0, cssWidth, cssHeight);

    ctx.fillStyle = "#334155";
    ctx.font = "18px Arial";
    ctx.textAlign = "center";
    ctx.fillText(mensaje, cssWidth / 2, cssHeight / 2);

    const ayuda = document.getElementById("graficaAyuda");
    if (ayuda) {
        ayuda.textContent = mensaje;
    }
}

function dibujarGraficaEnCanvas(expresionTurboX) {
    const canvas = document.getElementById("graficaCanvas");

    if (!canvas) {
        return;
    }

    asegurarPanelGraficaVisible();

    /*
       Tamaño estable del canvas:
       No dependemos de un rect en 0 cuando el panel acaba de activarse.
    */
    const contenedor = document.getElementById("graficaCanvasWrapper") || canvas.parentElement;
    const anchoDisponible = contenedor ? contenedor.clientWidth : 1000;
    const cssWidth = Math.max(720, Math.min(anchoDisponible - 36, 1160));
    const cssHeight = 420;

    canvas.style.width = cssWidth + "px";
    canvas.style.height = cssHeight + "px";

    const dpr = window.devicePixelRatio || 1;
    canvas.width = Math.floor(cssWidth * dpr);
    canvas.height = Math.floor(cssHeight * dpr);

    const ctx = canvas.getContext("2d");
    ctx.setTransform(dpr, 0, 0, dpr, 0, 0);

    const width = cssWidth;
    const height = cssHeight;

    const paddingLeft = 42;
    const paddingRight = 28;
    const paddingTop = 42;
    const paddingBottom = 38;

    const plotW = width - paddingLeft - paddingRight;
    const plotH = height - paddingTop - paddingBottom;

    const xMin = -10;
    const xMax = 10;

    let funcion;

    try {
        funcion = crearFuncionMatematica(expresionTurboX);
    } catch (error) {
        limpiarCanvasGrafica("No se pudo interpretar la función: " + error.message);
        return;
    }

    const puntos = [];

    for (let px = 0; px <= plotW; px++) {
        const x = xMin + (px / plotW) * (xMax - xMin);
        let y;

        try {
            y = funcion(x);
        } catch (error) {
            y = NaN;
        }

        if (Number.isFinite(y) && Math.abs(y) < 1000000) {
            puntos.push({ x, y });
        }
    }

    if (puntos.length < 2) {
        limpiarCanvasGrafica("No hay suficientes puntos válidos para dibujar la gráfica.");
        return;
    }

    let yMin = Math.min(...puntos.map((p) => p.y));
    let yMax = Math.max(...puntos.map((p) => p.y));

    if (yMin === yMax) {
        yMin -= 5;
        yMax += 5;
    } else {
        const margen = (yMax - yMin) * 0.15;
        yMin -= margen;
        yMax += margen;
    }

    if (yMin > -1 && yMax < 1) {
        yMin = -1;
        yMax = 1;
    }

    function mapX(x) {
        return paddingLeft + ((x - xMin) / (xMax - xMin)) * plotW;
    }

    function mapY(y) {
        return paddingTop + plotH - ((y - yMin) / (yMax - yMin)) * plotH;
    }

    ctx.clearRect(0, 0, width, height);

    const fondo = ctx.createLinearGradient(0, 0, 0, height);
    fondo.addColorStop(0, "#ffffff");
    fondo.addColorStop(1, "#f8fafc");
    ctx.fillStyle = fondo;
    ctx.fillRect(0, 0, width, height);

    // Borde interno
    ctx.strokeStyle = "#e2e8f0";
    ctx.lineWidth = 1;
    ctx.strokeRect(paddingLeft, paddingTop, plotW, plotH);

    // Cuadrícula vertical
    ctx.strokeStyle = "#e5e7eb";
    ctx.lineWidth = 1;

    for (let gx = -10; gx <= 10; gx++) {
        const px = mapX(gx);
        ctx.beginPath();
        ctx.moveTo(px, paddingTop);
        ctx.lineTo(px, paddingTop + plotH);
        ctx.stroke();
    }

    const pasoY = calcularPasoY(yMin, yMax);
    const inicioY = Math.ceil(yMin / pasoY) * pasoY;

    for (let gy = inicioY; gy <= yMax; gy += pasoY) {
        const py = mapY(gy);
        ctx.beginPath();
        ctx.moveTo(paddingLeft, py);
        ctx.lineTo(paddingLeft + plotW, py);
        ctx.stroke();
    }

    // Ejes principales
    ctx.strokeStyle = "#475569";
    ctx.lineWidth = 2;

    if (xMin <= 0 && xMax >= 0) {
        const ejeY = mapX(0);
        ctx.beginPath();
        ctx.moveTo(ejeY, paddingTop);
        ctx.lineTo(ejeY, paddingTop + plotH);
        ctx.stroke();
    }

    if (yMin <= 0 && yMax >= 0) {
        const ejeX = mapY(0);
        ctx.beginPath();
        ctx.moveTo(paddingLeft, ejeX);
        ctx.lineTo(paddingLeft + plotW, ejeX);
        ctx.stroke();
    }

    // Etiquetas X
    ctx.fillStyle = "#475569";
    ctx.font = "13px Arial";
    ctx.textAlign = "center";

    for (let gx = -10; gx <= 10; gx += 2) {
        const px = mapX(gx);
        const py = yMin <= 0 && yMax >= 0 ? mapY(0) + 18 : paddingTop + plotH - 8;
        ctx.fillText(String(gx), px, Math.min(py, height - 8));
    }

    // Etiquetas Y
    ctx.textAlign = "left";
    const etiquetasY = generarEtiquetasY(yMin, yMax, pasoY);

    etiquetasY.forEach((valor) => {
        const py = mapY(valor);
        if (py > paddingTop + 12 && py < paddingTop + plotH - 5) {
            ctx.fillText(formatearNumero(valor), paddingLeft + 8, py - 4);
        }
    });

    // Curva
    ctx.save();
    ctx.strokeStyle = "#2563eb";
    ctx.lineWidth = 3;
    ctx.shadowColor = "rgba(37, 99, 235, 0.28)";
    ctx.shadowBlur = 8;
    ctx.beginPath();

    let inicio = true;

    for (let px = 0; px <= plotW; px++) {
        const x = xMin + (px / plotW) * (xMax - xMin);
        let y;

        try {
            y = funcion(x);
        } catch (error) {
            inicio = true;
            continue;
        }

        if (!Number.isFinite(y) || Math.abs(y) > 1000000) {
            inicio = true;
            continue;
        }

        const canvasX = mapX(x);
        const canvasY = mapY(y);

        if (canvasY < -height * 2 || canvasY > height * 3) {
            inicio = true;
            continue;
        }

        if (inicio) {
            ctx.moveTo(canvasX, canvasY);
            inicio = false;
        } else {
            ctx.lineTo(canvasX, canvasY);
        }
    }

    ctx.stroke();
    ctx.restore();

    // Título
    ctx.fillStyle = "#0f172a";
    ctx.font = "bold 18px Arial";
    ctx.textAlign = "left";
    ctx.fillText("f(x) = " + expresionTurboX, paddingLeft + 10, 26);

    const ayuda = document.getElementById("graficaAyuda");
    if (ayuda) {
        ayuda.textContent = `Rango utilizado: x de ${xMin} a ${xMax}. Escala vertical aproximada: y de ${formatearNumero(yMin)} a ${formatearNumero(yMax)}.`;
    }
}

function crearFuncionMatematica(expresionTurboX) {
    let expr = expresionTurboX.trim();

    /*
       Se permite una expresión matemática segura para el canvas.
       Funciones aceptadas:
       sin, cos, tan, sqrt, abs, log, ln, exp
    */
    if (!/^[0-9xX+\-*/%^().,\sA-Za-z_]+$/.test(expr)) {
        throw new Error("solo se permiten números, x, funciones matemáticas, paréntesis y operadores + - * / % ^");
    }

    const funcionesPermitidas = new Set([
        "sin", "cos", "tan", "sqrt", "abs", "log", "ln", "exp",
        "SIN", "COS", "TAN", "SQRT", "ABS", "LOG", "LN", "EXP"
    ]);

    const identificadores = expr.match(/\b[A-Za-z_][A-Za-z0-9_]*\b/g) || [];

    for (const id of identificadores) {
        if (id !== "x" && id !== "X" && !funcionesPermitidas.has(id)) {
            throw new Error("identificador no permitido en la gráfica: " + id);
        }
    }

    expr = expr.replaceAll(",", ".");
    expr = expr.replaceAll("^", "**");
    expr = expr.replace(/\bX\b/g, "x");

    expr = expr.replace(/\bsin\s*\(/gi, "Math.sin(");
    expr = expr.replace(/\bcos\s*\(/gi, "Math.cos(");
    expr = expr.replace(/\btan\s*\(/gi, "Math.tan(");
    expr = expr.replace(/\bsqrt\s*\(/gi, "Math.sqrt(");
    expr = expr.replace(/\babs\s*\(/gi, "Math.abs(");
    expr = expr.replace(/\bexp\s*\(/gi, "Math.exp(");
    expr = expr.replace(/\bln\s*\(/gi, "Math.log(");
    expr = expr.replace(/\blog\s*\(/gi, "Math.log10(");

    return new Function("x", `
        const resultado = ${expr};
        return Number(resultado);
    `);
}

function calcularPasoY(yMin, yMax) {
    const rango = Math.abs(yMax - yMin);

    if (rango <= 5) return 1;
    if (rango <= 20) return 2;
    if (rango <= 50) return 5;
    if (rango <= 100) return 10;
    if (rango <= 500) return 50;
    if (rango <= 1000) return 100;

    return Math.pow(10, Math.floor(Math.log10(rango)) - 1);
}

function generarEtiquetasY(yMin, yMax, pasoY) {
    const etiquetas = [];
    const inicio = Math.ceil(yMin / pasoY) * pasoY;

    for (let y = inicio; y <= yMax; y += pasoY) {
        etiquetas.push(y);
    }

    return etiquetas;
}

function formatearNumero(valor) {
    if (Math.abs(valor) >= 1000) {
        return valor.toFixed(0);
    }

    if (Math.abs(valor) >= 10) {
        return valor.toFixed(1).replace(/\.0$/, "");
    }

    return valor.toFixed(2).replace(/\.00$/, "").replace(/0$/, "");
}

btnAbrirGrafica.addEventListener("click", () => {
    if (!ultimaUrlGrafica) {
        return;
    }

    let urlFinal = ultimaUrlGrafica;

    if (urlFinal.startsWith("/")) {
        urlFinal = API_BASE + urlFinal;
    }

    window.open(urlFinal, "_blank");
});

/* =========================================================
   EJECUCIÓN / INTÉRPRETE TURBO X
   ========================================================= */

async function ejecutarCodigoDesdeUI() {
    const fuente = codigo.value.trim();

    if (!fuente) {
        escribirSalidaPrograma("No hay código para ejecutar.");
        log("No hay código para ejecutar.", "Advertencia");
        return;
    }

    if (btnEjecutarCodigo) {
        btnEjecutarCodigo.disabled = true;
        btnEjecutarCodigo.textContent = "Validando...";
    }

    if (btnEjecutarPanel) {
        btnEjecutarPanel.disabled = true;
        btnEjecutarPanel.textContent = "Validando...";
    }

    activarPanel("executionPanel");
    limpiarTerminalInteractiva();
    escribirSalidaPrograma("Validando código antes de ejecutar...");

    try {
        // FASE 1: Léxico
        log("Validando fase léxica antes de ejecutar...");
        const tokens = await analizarLexico(fuente);
        renderTokens(tokens);
        renderResumenLexico(tokens);

        const totalErroresLexicos = contarErroresLexicos(tokens);

        if (totalErroresLexicos > 0) {
            renderSintacticoNoEjecutado("No se ejecutó el análisis sintáctico porque existen errores léxicos.");
            renderSemanticoNoEjecutado("No se ejecutó el análisis semántico porque existen errores léxicos.");
            renderGraficaNoEjecutada("No se generó gráfica porque existen errores léxicos.");
            escribirSalidaPrograma("No se puede ejecutar el programa porque existen errores léxicos.");
            log("Ejecución detenida por errores léxicos.", "Error");
            return;
        }

        // FASE 2: Sintaxis
        log("Validando fase sintáctica antes de ejecutar...");
        const resultadoSintactico = await analizarSintactico(fuente);
        renderSintactico(resultadoSintactico);

        if (!resultadoSintactico || !resultadoSintactico.correcto) {
            renderSemanticoNoEjecutado("No se ejecutó el análisis semántico porque existen errores sintácticos.");
            renderGraficaNoEjecutada("No se generó gráfica porque existen errores sintácticos.");
            escribirSalidaPrograma("No se puede ejecutar el programa porque existen errores sintácticos.");
            log("Ejecución detenida por errores sintácticos.", "Error");
            return;
        }

        // FASE 3: Semántica
        log("Validando fase semántica antes de ejecutar...");
        const resultadoSemantico = await analizarSemantico(fuente);
        renderSemantico(resultadoSemantico);

        if (!resultadoSemantico || !resultadoSemantico.correcto) {
            renderGraficaNoEjecutada("No se generó gráfica porque existen errores semánticos.");
            escribirSalidaPrograma("No se puede ejecutar el programa porque existen errores semánticos.");
            log("Ejecución detenida por errores semánticos.", "Error");
            return;
        }

        // FASE 4: Ejecución interactiva
        log("Iniciando terminal interactiva Turbo X...");
        codigoEjecucionInteractiva = fuente;
        entradasInteractivas = [];
        historialEntradasTerminal = [];
        salidaPendienteLeer = [];

        await ejecutarProgramaInteractivo();

    } catch (error) {
        escribirSalidaPrograma("Error durante la ejecución:\n" + error.message);
        ocultarEntradaTerminal();
        log("No se pudo ejecutar el programa.", "Error");
        log(error.message, "Detalle");
    } finally {
        if (btnEjecutarCodigo) {
            btnEjecutarCodigo.disabled = false;
            btnEjecutarCodigo.textContent = "Ejecutar código";
        }

        if (btnEjecutarPanel) {
            btnEjecutarPanel.disabled = false;
            btnEjecutarPanel.textContent = "Ejecutar";
        }
    }
}


async function ejecutarProgramaInteractivo() {
    ejecucionInteractivaActiva = true;
    ocultarEntradaTerminal(false);

    const entradasTexto = entradasInteractivas.join("\n");
    const resultado = await ejecutarProgramaBackend(codigoEjecucionInteractiva, entradasTexto);

    if (necesitaEntradaLeer(resultado)) {
        salidaPendienteLeer = resultado.salida || [];
        renderTerminal(resultado.salida || [], true);
        renderTablaVariablesRuntime(resultado.variables || {});
        mostrarEntradaTerminal();
        log("El programa espera una entrada para LEER.");
        return;
    }

    ocultarEntradaTerminal();

    if (resultado.correcto) {
        renderTerminal(resultado.salida || [], false);
        renderTablaVariablesRuntime(resultado.variables || {});
        log("Ejecución finalizada correctamente.");
    } else {
        renderTerminal(resultado.salida || [], false, resultado.errores || []);
        renderTablaVariablesRuntime(resultado.variables || {});
        log("Ejecución finalizada con errores.", "Error");
    }

    ejecucionInteractivaActiva = false;
}

function necesitaEntradaLeer(resultado) {
    if (!resultado || !resultado.errores || resultado.errores.length === 0) {
        return false;
    }

    return resultado.errores.some((error) =>
        String(error).toUpperCase().includes("LEER(") &&
        String(error).toLowerCase().includes("necesita una entrada")
    );
}

function renderTerminal(salida, esperandoEntrada, errores = []) {
    const lineas = construirLineasTerminal(salida || []);

    if (errores && errores.length > 0) {
        lineas.push("");
        lineas.push("ERRORES DE EJECUCIÓN");
        lineas.push("--------------------");
        errores.forEach((error, index) => {
            lineas.push((index + 1) + ". " + error);
        });
    }

    if (salidaPrograma) {
        salidaPrograma.textContent = lineas.length > 0
                ? lineas.join("\n")
                : "(El programa no generó salida con IMPRIMIR)";
    }

    if (terminalWindow) {
        terminalWindow.scrollTop = terminalWindow.scrollHeight;
    }

    if (esperandoEntrada) {
        mostrarEntradaTerminal();
    }
}

function construirLineasTerminal(salida) {
    const resultado = [];
    const historialOrdenado = [...historialEntradasTerminal].sort((a, b) => a.posicionSalida - b.posicionSalida);
    let indiceHistorial = 0;

    for (let i = 0; i <= salida.length; i++) {
        while (
            indiceHistorial < historialOrdenado.length &&
            historialOrdenado[indiceHistorial].posicionSalida === i
        ) {
            resultado.push("> " + historialOrdenado[indiceHistorial].valor);
            indiceHistorial++;
        }

        if (i < salida.length) {
            resultado.push(salida[i]);
        }
    }

    return resultado;
}

function mostrarEntradaTerminal() {
    if (terminalInputRow) {
        terminalInputRow.classList.remove("hidden");
    }

    if (terminalInput) {
        terminalInput.disabled = false;
        terminalInput.value = "";
        setTimeout(() => terminalInput.focus(), 50);
    }

    if (terminalWindow) {
        terminalWindow.scrollTop = terminalWindow.scrollHeight;
    }
}

function ocultarEntradaTerminal(limpiar = true) {
    if (terminalInputRow) {
        terminalInputRow.classList.add("hidden");
    }

    if (terminalInput) {
        terminalInput.disabled = true;

        if (limpiar) {
            terminalInput.value = "";
        }
    }
}

function limpiarTerminalInteractiva() {
    entradasInteractivas = [];
    historialEntradasTerminal = [];
    salidaPendienteLeer = [];
    ejecucionInteractivaActiva = false;
    ocultarEntradaTerminal();
    limpiarTablaVariablesRuntime();

    if (salidaPrograma) {
        salidaPrograma.textContent = "Terminal lista. Presiona “Ejecutar código” para iniciar.";
    }
}

async function enviarEntradaTerminal() {
    if (!terminalInput || terminalInput.disabled) {
        return;
    }

    const valor = terminalInput.value;
    entradasInteractivas.push(valor);

    /*
       Guardamos la posición donde el usuario escribió la entrada.
       Así se ve como terminal real:
       ingrese su edad
       > 5
       5
    */
    historialEntradasTerminal.push({
        posicionSalida: salidaPendienteLeer.length,
        valor
    });

    terminalInput.value = "";
    ocultarEntradaTerminal(false);

    try {
        await ejecutarProgramaInteractivo();
    } catch (error) {
        escribirSalidaPrograma("Error durante la ejecución:\n" + error.message);
        ocultarEntradaTerminal();
        log("No se pudo continuar la ejecución interactiva.", "Error");
    }
}


function renderResultadoEjecucion(resultado) {
    if (!resultado) {
        escribirSalidaPrograma("No se recibió respuesta del módulo de ejecución.");
        limpiarTablaVariablesRuntime();
        return;
    }

    let texto = "";

    texto += "EJECUCIÓN TURBO X\n";
    texto += "=================\n\n";
    texto += "Estado: " + (resultado.correcto ? "Correcta" : "Con errores") + "\n";
    texto += "Mensaje: " + (resultado.mensaje || "Sin mensaje") + "\n";
    texto += "Instrucciones ejecutadas: " + (resultado.instruccionesEjecutadas || 0) + "\n\n";

    if (resultado.salida && resultado.salida.length > 0) {
        texto += "SALIDA DEL PROGRAMA\n";
        texto += "-------------------\n";
        texto += resultado.salida.join("\n") + "\n\n";
    } else {
        texto += "SALIDA DEL PROGRAMA\n";
        texto += "-------------------\n";
        texto += "(El programa no generó salida con IMPRIMIR)\n\n";
    }

    if (resultado.errores && resultado.errores.length > 0) {
        texto += "ERRORES DE EJECUCIÓN\n";
        texto += "--------------------\n";
        resultado.errores.forEach((error, index) => {
            texto += (index + 1) + ". " + error + "\n";
        });
    }

    escribirSalidaPrograma(texto.trimEnd());
    renderTablaVariablesRuntime(resultado.variables || {});
}

function escribirSalidaPrograma(texto) {
    if (salidaPrograma) {
        salidaPrograma.textContent = texto;
    }
}

function renderTablaVariablesRuntime(variables) {
    if (!tablaVariablesRuntime) {
        return;
    }

    const nombres = Object.keys(variables || {});

    if (nombres.length === 0) {
        tablaVariablesRuntime.innerHTML = '<tr><td colspan="3" class="empty-row">No hay variables en memoria.</td></tr>';
        return;
    }

    tablaVariablesRuntime.innerHTML = nombres.map((nombre) => {
        const variable = variables[nombre] || {};
        return `
            <tr>
                <td>${escapeHtml(nombre)}</td>
                <td>${escapeHtml(variable.tipo || "DESCONOCIDO")}</td>
                <td>${escapeHtml(variable.valorTexto || "")}</td>
            </tr>
        `;
    }).join("");
}

function limpiarTablaVariablesRuntime() {
    if (tablaVariablesRuntime) {
        tablaVariablesRuntime.innerHTML = '<tr><td colspan="3" class="empty-row">Sin ejecución todavía.</td></tr>';
    }
}

if (btnEjecutarCodigo) {
    btnEjecutarCodigo.addEventListener("click", ejecutarCodigoDesdeUI);
}

if (btnEjecutarPanel) {
    btnEjecutarPanel.addEventListener("click", ejecutarCodigoDesdeUI);
}

if (btnLimpiarEjecucion) {
    btnLimpiarEjecucion.addEventListener("click", () => {
        limpiarTerminalInteractiva();
        log("Terminal de ejecución limpiada.");
    });
}

if (btnLimpiarEntradas) {
    btnLimpiarEntradas.addEventListener("click", () => {
        if (entradaPrograma) {
            entradaPrograma.value = "";
        }

        entradasInteractivas = [];
        historialEntradasTerminal = [];
        salidaPendienteLeer = [];
        log("Entradas de LEER limpiadas.");
    });
}


if (terminalInput) {
    terminalInput.addEventListener("keydown", async (event) => {
        if (event.key === "Enter") {
            event.preventDefault();
            await enviarEntradaTerminal();
        }
    });
}


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
    lineasConError.clear();
    lineasConAdvertencia.clear();
    renderMarcasLineas();

    tablaTokens.innerHTML = '<tr><td colspan="5" class="empty-row">Presiona “Compilar código” para visualizar los tokens.</td></tr>';
    tablaSimbolos.innerHTML = '<tr><td colspan="4" class="empty-row">Sin datos semánticos todavía.</td></tr>';

    parserStatus.className = "result-box waiting";
    parserStatus.textContent = "Pendiente de ejecutar JCUP.";

    semanticErrors.className = "result-box waiting";
    semanticErrors.textContent = "Pendiente de validar tipos con Java.";

    astOutput.textContent = "AST pendiente...";

    funcionDetectada.textContent = "f(x) = pendiente";
    urlGrafica.textContent = "No se ha generado una URL.";
    btnAbrirGrafica.disabled = true;
    ultimaUrlGrafica = "";

    limpiarCanvasGrafica("Gráfica pendiente. Compila un programa con GRAFICAR para visualizarla.");

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


/* =========================================================
   EJEMPLOS, REPORTE Y AUTOGUARDADO
   ========================================================= */

function obtenerEjemploTurboX(tipo) {
    const ejemplos = {
        correcto: `PROGRAMA PruebaCorrecta
INICIO
    ENTERO edad = 18;
    ENTERO contador = 0;
    REAL total = 10 + 5.5;
    REAL promedio = total / 2;
    CADENA nombre = "Carlos";
    CARACTER letra = 'A';
    LOGICO activo = VERDADERO;
    LOGICO mayorEdad = edad >= 18;

    IMPRIMIR(nombre);
    IMPRIMIR(edad);
    IMPRIMIR(total);

    edad = edad + 1;
    total = total + promedio;
    activo = mayorEdad Y VERDADERO;

    SI (edad >= 18 Y activo) ENTONCES {
        IMPRIMIR("La persona es mayor de edad");
    } SINO {
        IMPRIMIR("La persona no cumple la condicion");
    }

    MIENTRAS (contador < 3) HACER {
        IMPRIMIR(contador);
        contador = contador + 1;
    }

    GRAFICAR f(x) = x ^ 2 + x + 2;
FIN`,

        lexico: `PROGRAMA ErrorLexico
INICIO
    ENTERO edad = 18;
    edad = edad @ 2;
    IMPRIMIR(edad);
FIN`,

        sintactico: `PROGRAMA ErrorSintactico
INICIO
    ENTERO edad = 18
    REAL total = 10 + 5.5;
    IMPRIMIR(edad);
FIN`,

        semantico: `PROGRAMA ErrorSemantico
INICIO
    ENTERO edad = "hola";
    REAL total = 10 + 5.5;
    edad = total;
    IMPRIMIR(apellido);

    ENTERO edad = 20;

    SI ("texto") ENTONCES {
        IMPRIMIR("Condicion incorrecta");
    }
FIN`,

        grafica: `PROGRAMA GraficaCuadratica
INICIO
    REAL valor = 5.5;
    IMPRIMIR(valor);

    GRAFICAR f(x) = x ^ 2 + x + 2;
FIN`,

        trigonometrica: `PROGRAMA GraficaTrigonometrica
INICIO
    REAL valor = 1.5;
    IMPRIMIR(valor);

    GRAFICAR f(x) = sin(x) + cos(x);
FIN`,

        ejecucionLeer: `PROGRAMA EjecutarLeer
INICIO
    CADENA nombre;
    ENTERO edad;

    IMPRIMIR("Ingrese su nombre:");
    LEER(nombre);

    IMPRIMIR("Ingrese su edad:");
    LEER(edad);

    IMPRIMIR(nombre);
    IMPRIMIR(edad + 1);
FIN`,

        divisionCero: `PROGRAMA DivisionCero
INICIO
    ENTERO numero = 10 / 0;
    REAL residuo = 20 % 0;
    IMPRIMIR(numero);
FIN`
    };

    return ejemplos[tipo] || ejemplos.correcto;
}

function obtenerNombreEjemplo(tipo) {
    const nombres = {
        correcto: "Código correcto",
        lexico: "Error léxico",
        sintactico: "Error sintáctico",
        semantico: "Error semántico",
        grafica: "Gráfica cuadrática",
        trigonometrica: "Gráfica trigonométrica",
        divisionCero: "División entre cero",
        ejecucionLeer: "Ejecución con LEER"
    };

    return nombres[tipo] || "Código correcto";
}

function crearReporteBase(fuente) {
    return {
        fecha: new Date().toLocaleString(),
        codigo: fuente,
        tokens: [],
        resultadoSintactico: null,
        resultadoSemantico: null,
        resultadoGrafica: null,
        estadoFinal: "Pendiente"
    };
}

function descargarReporteCompilacion() {
    const reporte = construirTextoReporte();
    const blob = new Blob([reporte], { type: "text/plain;charset=utf-8" });
    const url = URL.createObjectURL(blob);

    const enlace = document.createElement("a");
    enlace.href = url;
    enlace.download = "reporte_compilacion_turbo_x.txt";
    document.body.appendChild(enlace);
    enlace.click();
    document.body.removeChild(enlace);

    URL.revokeObjectURL(url);

    log("Reporte de compilación descargado.");
}

function construirTextoReporte() {
    const r = ultimoReporteCompilacion || crearReporteBase(codigo.value.trim());

    const linea = "=".repeat(78);
    const sublinea = "-".repeat(78);

    function valorSeguro(valor) {
        if (valor === null || valor === undefined || valor === "") {
            return "N/A";
        }

        return String(valor);
    }

    function siNo(valor) {
        return valor ? "Sí" : "No";
    }

    function estadoTexto(valor) {
        return valor ? "Correcto" : "Con errores";
    }

    function fila(columnas, anchos) {
        return columnas.map((columna, index) => {
            const texto = valorSeguro(columna).replace(/\s+/g, " ");
            const ancho = anchos[index];

            if (texto.length > ancho) {
                return texto.substring(0, ancho - 3) + "...";
            }

            return texto.padEnd(ancho, " ");
        }).join(" | ");
    }

    const tokens = r.tokens || [];
    const erroresLexicos = contarErroresLexicos(tokens);
    const resultadoSintactico = r.resultadoSintactico;
    const resultadoSemantico = r.resultadoSemantico;
    const resultadoGrafica = r.resultadoGrafica;

    let texto = "";

    texto += linea + "\n";
    texto += "REPORTE DE COMPILACIÓN - TURBO X\n";
    texto += linea + "\n\n";

    texto += "1. RESUMEN GENERAL\n";
    texto += sublinea + "\n";
    texto += "Fecha de generación : " + valorSeguro(r.fecha || new Date().toLocaleString()) + "\n";
    texto += "Estado final        : " + valorSeguro(r.estadoFinal || "Pendiente") + "\n";
    texto += "Total de tokens     : " + tokens.length + "\n";
    texto += "Errores léxicos     : " + erroresLexicos + "\n";
    texto += "Sintaxis            : " + (resultadoSintactico ? estadoTexto(resultadoSintactico.correcto) : "No ejecutada") + "\n";
    texto += "Semántica           : " + (resultadoSemantico ? estadoTexto(resultadoSemantico.correcto) : "No ejecutada") + "\n";
    texto += "Gráfica             : " + (resultadoGrafica && resultadoGrafica.graficaEncontrada ? "Generada" : "No generada") + "\n\n";

    texto += "2. CÓDIGO FUENTE\n";
    texto += sublinea + "\n";
    const codigoFuente = r.codigo || codigo.value || "";
    const lineasCodigo = codigoFuente.split("\n");

    lineasCodigo.forEach((lineaCodigo, index) => {
        const numero = String(index + 1).padStart(3, " ");
        texto += numero + " | " + lineaCodigo + "\n";
    });

    texto += "\n";

    texto += "3. ANÁLISIS LÉXICO\n";
    texto += sublinea + "\n";
    texto += "Total de tokens reconocidos: " + tokens.length + "\n";
    texto += "Errores léxicos detectados : " + erroresLexicos + "\n\n";

    if (tokens.length > 0) {
        const anchos = [5, 22, 24, 8, 8];
        texto += fila(["No.", "Tipo", "Lexema", "Línea", "Columna"], anchos) + "\n";
        texto += "-".repeat(78) + "\n";

        tokens.forEach((token, index) => {
            texto += fila([
                index + 1,
                token.tipo,
                token.lexema,
                token.linea,
                token.columna
            ], anchos) + "\n";
        });

        texto += "\n";
    }

    texto += "4. ANÁLISIS SINTÁCTICO\n";
    texto += sublinea + "\n";

    if (resultadoSintactico) {
        texto += "Estado  : " + estadoTexto(resultadoSintactico.correcto) + "\n";
        texto += "Mensaje : " + valorSeguro(resultadoSintactico.mensaje) + "\n";

        const errores = resultadoSintactico.errores || [];

        if (errores.length > 0) {
            texto += "\nErrores sintácticos:\n";
            errores.forEach((error, index) => {
                texto += "  " + (index + 1) + ". " + error + "\n";
            });
        }
    } else {
        texto += "No ejecutado o sin datos disponibles.\n";
    }

    texto += "\n";

    texto += "5. ANÁLISIS SEMÁNTICO\n";
    texto += sublinea + "\n";

    if (resultadoSemantico) {
        texto += "Estado  : " + estadoTexto(resultadoSemantico.correcto) + "\n";
        texto += "Mensaje : " + valorSeguro(resultadoSemantico.mensaje) + "\n";

        const errores = resultadoSemantico.errores || [];

        if (errores.length > 0) {
            texto += "\nErrores semánticos:\n";
            errores.forEach((error, index) => {
                texto += "  " + (index + 1) + ". Línea " + valorSeguro(error.linea)
                    + " | " + valorSeguro(error.lexema)
                    + " | " + valorSeguro(error.descripcion) + "\n";
            });
        }

        const simbolos = resultadoSemantico.tablaSimbolos || [];

        if (simbolos.length > 0) {
            texto += "\nTabla de símbolos:\n";
            const anchos = [22, 14, 22, 8, 14];
            texto += fila(["Nombre", "Tipo", "Categoría", "Línea", "Estado"], anchos) + "\n";
            texto += "-".repeat(78) + "\n";

            simbolos.forEach((simbolo) => {
                texto += fila([
                    simbolo.nombre,
                    simbolo.tipo,
                    simbolo.categoria || "VARIABLE",
                    simbolo.lineaDeclaracion,
                    simbolo.inicializado ? "Inicializada" : "Declarada"
                ], anchos) + "\n";
            });
        }
    } else {
        texto += "No ejecutado o sin datos disponibles.\n";
    }

    texto += "\n";

    texto += "6. MÓDULO DE GRÁFICAS\n";
    texto += sublinea + "\n";

    if (resultadoGrafica) {
        texto += "Estado              : " + estadoTexto(resultadoGrafica.correcta) + "\n";
        texto += "Mensaje             : " + valorSeguro(resultadoGrafica.mensaje) + "\n";
        texto += "Gráfica encontrada  : " + siNo(resultadoGrafica.graficaEncontrada) + "\n";
        texto += "Función             : " + valorSeguro(resultadoGrafica.funcion) + "\n";
        texto += "Variable            : " + valorSeguro(resultadoGrafica.variable) + "\n";
        texto += "Expresión           : " + valorSeguro(resultadoGrafica.expresion) + "\n";
        texto += "URL Desmos          : " + valorSeguro(resultadoGrafica.url) + "\n";
    } else {
        texto += "No ejecutado o sin datos disponibles.\n";
    }

    texto += "\n";
    texto += linea + "\n";
    texto += "Fin del reporte.\n";
    texto += linea + "\n";

    return texto;
}

function actualizarEstadisticasEditor() {
    if (!editorStats || !codigo) {
        return;
    }

    const texto = codigo.value;
    const totalLineas = texto.length === 0 ? 1 : texto.split("\n").length;
    const caracteres = texto.length;

    editorStats.textContent = `Líneas: ${totalLineas} | Caracteres: ${caracteres}`;
}

function programarAutoguardado() {
    if (!codigo) {
        return;
    }

    if (autosaveTimer) {
        clearTimeout(autosaveTimer);
    }

    autosaveTimer = setTimeout(() => {
        guardarCodigoLocal();
    }, 500);
}

function guardarCodigoLocal() {
    localStorage.setItem(CODIGO_STORAGE_KEY, codigo.value);

    if (autosaveStatus) {
        autosaveStatus.textContent = "Guardado: " + new Date().toLocaleTimeString();
    }
}

function cargarCodigoLocal() {
    const codigoGuardado = localStorage.getItem(CODIGO_STORAGE_KEY);

    if (codigoGuardado && !codigo.value.trim()) {
        codigo.value = codigoGuardado;
        fileName.textContent = "autoguardado_local.tx";
    }
}


function ejemploTurboX() {
    return `PROGRAMA SistemaNotas
INICIO

    // Declaración de variables
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
            IMPRIMIR("Tiene 18 años");
            PARAR;

        CASO 25:
            IMPRIMIR("Tiene 25 años");
            PARAR;

        OTRO:
            IMPRIMIR("Edad diferente");
            PARAR;
    }

    GRAFICAR f(x) = x ^ 2 + 3;

FIN`;
}

/* =========================================================
   INICIALIZACIÓN
   ========================================================= */

cargarCodigoLocal();
inicializarSnippetsEditor();
actualizarLineas();
inicializarTema();
limpiarCanvasGrafica("Gráfica pendiente. Compila un programa con GRAFICAR para visualizarla.");
log("Interfaz lista. Backend esperado en http://localhost:8080.");
