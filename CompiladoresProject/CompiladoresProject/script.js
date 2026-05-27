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

const codigo = document.getElementById("codigo");
const lineNumbers = document.getElementById("lineNumbers");
const archivoTxt = document.getElementById("archivoTxt");
const btnArchivo = document.getElementById("btnArchivo");
const btnEjemplo = document.getElementById("btnEjemplo");
const btnTema = document.getElementById("btnTema");
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
        const panel = document.getElementById(panelId);

        if (panel) {
            panel.classList.add("active-panel");
        }
    });
});


/* =========================================================
   CAMBIO DE TEMA CLARO / OSCURO
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

    if (registrarLog && typeof log === "function") {
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
    log("Código de ejemplo cargado.");
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
        log("Código copiado al portapapeles.");
    } catch (error) {
        log("No se pudo copiar el código.", "Error");
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
        log("No hay código para compilar.", "Advertencia");
        return;
    }

    limpiarResultados(false);

    btnCompilar.disabled = true;
    btnCompilar.textContent = "Compilando...";

    log("Iniciando compilación por fases...");

    try {
        serverStatus.textContent = "Conectando...";

        /* FASE 1: ANÁLISIS LÉXICO */
        log("Ejecutando análisis léxico...");
        const tokens = await analizarLexico(fuente);

        renderTokens(tokens);
        renderResumenLexico(tokens);

        const totalErroresLexicos = contarErroresLexicos(tokens);

        if (totalErroresLexicos > 0) {
            renderSintacticoNoEjecutado("No se ejecutó el análisis sintáctico porque existen errores léxicos.");
            renderSemanticoNoEjecutado("No se ejecutó el análisis semántico porque existen errores léxicos.");
            renderGraficaNoEjecutada("No se generó gráfica porque existen errores léxicos.");

            serverStatus.textContent = "Conectado";
            log("Análisis léxico finalizado con " + totalErroresLexicos + " error(es).", "Error");
            log("Compilación detenida en la fase léxica.", "Sistema");
            return;
        }

        log("Análisis léxico completado correctamente.");

        /* FASE 2: ANÁLISIS SINTÁCTICO */
        log("Ejecutando análisis sintáctico con JCUP...");
        const resultadoSintactico = await analizarSintactico(fuente);

        renderSintactico(resultadoSintactico);

        if (!resultadoSintactico || !resultadoSintactico.correcto) {
            renderSemanticoNoEjecutado("No se ejecutó el análisis semántico porque existen errores sintácticos.");
            renderGraficaNoEjecutada("No se generó gráfica porque existen errores sintácticos.");

            serverStatus.textContent = "Conectado";
            log("Análisis sintáctico finalizado con errores.", "Error");
            log("Compilación detenida en la fase sintáctica.", "Sistema");
            return;
        }

        log("Análisis sintáctico completado correctamente.");

        /* FASE 3: ANÁLISIS SEMÁNTICO */
        log("Ejecutando análisis semántico...");
        const resultadoSemantico = await analizarSemantico(fuente);

        renderSemantico(resultadoSemantico);

        if (!resultadoSemantico || !resultadoSemantico.correcto) {
            renderGraficaNoEjecutada("No se generó gráfica porque existen errores semánticos.");

            const cantidadErrores = resultadoSemantico && resultadoSemantico.errores
                ? resultadoSemantico.errores.length
                : 0;

            serverStatus.textContent = "Conectado";
            log("Análisis semántico finalizado con " + cantidadErrores + " error(es).", "Error");
            log("Compilación finalizada con errores semánticos.", "Sistema");
            return;
        }

        log("Análisis semántico completado correctamente.");

        /* FASE 4: MÓDULO DE GRÁFICAS CON JCUP + DESMOS */
        log("Procesando módulo de gráficas desde JCUP...");
        const resultadoGrafica = await procesarGraficaConJCUP(fuente);

        renderGraficaDesdeBackend(resultadoGrafica);

        serverStatus.textContent = "Conectado";
        log("Compilación finalizada correctamente.");

    } catch (error) {
        serverStatus.textContent = "Error de conexión";
        log("No se pudo conectar correctamente con el backend. Verifica que Spring Boot esté corriendo en localhost:8080.", "Error");
        log(error.message, "Detalle");

        renderSintacticoNoEjecutado("No se recibió respuesta correcta del backend.");
        renderSemanticoNoEjecutado("No se recibió respuesta correcta del backend.");
        renderGraficaNoEjecutada("No se generó gráfica por error de conexión.");

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

    dibujarGraficaEnCanvas(resultado.expresion);

    log("URL de gráfica generada por JCUP: " + resultado.url);
}

function renderGraficaNoEjecutada(motivo) {
    funcionDetectada.textContent = "f(x) = no ejecutada";
    urlGrafica.textContent = motivo;
    btnAbrirGrafica.disabled = true;
    ultimaUrlGrafica = "";
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
    const canvas = asegurarCanvasGrafica();
    const ctx = canvas.getContext("2d");
    const width = canvas.width;
    const height = canvas.height;

    ctx.clearRect(0, 0, width, height);
    ctx.fillStyle = "#ffffff";
    ctx.fillRect(0, 0, width, height);

    ctx.fillStyle = "#334155";
    ctx.font = "18px Arial";
    ctx.textAlign = "center";
    ctx.fillText(mensaje, width / 2, height / 2);

    const ayuda = document.getElementById("graficaAyuda");
    if (ayuda) {
        ayuda.textContent = mensaje;
    }
}

function dibujarGraficaEnCanvas(expresionTurboX) {
    const canvas = asegurarCanvasGrafica();
    const ctx = canvas.getContext("2d");

    const width = canvas.width;
    const height = canvas.height;

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

    for (let px = 0; px <= width; px++) {
        const x = xMin + (px / width) * (xMax - xMin);
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
        return ((x - xMin) / (xMax - xMin)) * width;
    }

    function mapY(y) {
        return height - ((y - yMin) / (yMax - yMin)) * height;
    }

    ctx.clearRect(0, 0, width, height);
    ctx.fillStyle = "#ffffff";
    ctx.fillRect(0, 0, width, height);

    /* Cuadrícula */
    ctx.strokeStyle = "#e2e8f0";
    ctx.lineWidth = 1;

    for (let gx = -10; gx <= 10; gx++) {
        const px = mapX(gx);
        ctx.beginPath();
        ctx.moveTo(px, 0);
        ctx.lineTo(px, height);
        ctx.stroke();
    }

    const pasoY = calcularPasoY(yMin, yMax);
    const inicioY = Math.ceil(yMin / pasoY) * pasoY;

    for (let gy = inicioY; gy <= yMax; gy += pasoY) {
        const py = mapY(gy);
        ctx.beginPath();
        ctx.moveTo(0, py);
        ctx.lineTo(width, py);
        ctx.stroke();
    }

    /* Ejes */
    ctx.strokeStyle = "#475569";
    ctx.lineWidth = 2;

    if (xMin <= 0 && xMax >= 0) {
        const ejeY = mapX(0);
        ctx.beginPath();
        ctx.moveTo(ejeY, 0);
        ctx.lineTo(ejeY, height);
        ctx.stroke();
    }

    if (yMin <= 0 && yMax >= 0) {
        const ejeX = mapY(0);
        ctx.beginPath();
        ctx.moveTo(0, ejeX);
        ctx.lineTo(width, ejeX);
        ctx.stroke();
    }

    /* Etiquetas */
    ctx.fillStyle = "#334155";
    ctx.font = "13px Arial";
    ctx.textAlign = "center";

    for (let gx = -10; gx <= 10; gx += 2) {
        const px = mapX(gx);
        const py = yMin <= 0 && yMax >= 0 ? mapY(0) + 18 : height - 10;
        ctx.fillText(String(gx), px, py);
    }

    ctx.textAlign = "left";
    const etiquetasY = generarEtiquetasY(yMin, yMax, pasoY);

    etiquetasY.forEach((valor) => {
        const py = mapY(valor);
        if (py > 12 && py < height - 5) {
            ctx.fillText(formatearNumero(valor), 8, py - 4);
        }
    });

    /* Curva */
    ctx.strokeStyle = "#2563eb";
    ctx.lineWidth = 3;
    ctx.beginPath();

    let inicio = true;

    for (let px = 0; px <= width; px++) {
        const x = xMin + (px / width) * (xMax - xMin);
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

    /* Título dentro del canvas */
    ctx.fillStyle = "#0f172a";
    ctx.font = "bold 16px Arial";
    ctx.textAlign = "left";
    ctx.fillText("f(x) = " + expresionTurboX, 16, 26);

    const ayuda = document.getElementById("graficaAyuda");
    if (ayuda) {
        ayuda.textContent = `Rango utilizado: x de ${xMin} a ${xMax}. Escala vertical aproximada: y de ${formatearNumero(yMin)} a ${formatearNumero(yMax)}.`;
    }
}

function crearFuncionMatematica(expresionTurboX) {
    let expr = expresionTurboX.trim();

    /*
       Se permite una expresión matemática sencilla.
       Esto evita ejecutar texto peligroso con Function.
       Permitido:
       - números
       - x o X
       - operadores + - * / % ^
       - paréntesis
       - punto decimal o coma decimal
    */
    if (!/^[0-9xX+\-*/%^().,\s]+$/.test(expr)) {
        throw new Error("solo se permiten números, x, paréntesis y operadores + - * / % ^");
    }

    expr = expr.replaceAll(",", ".");
    expr = expr.replaceAll("^", "**");
    expr = expr.replace(/\bX\b/g, "x");

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

actualizarLineas();
inicializarTema();
limpiarCanvasGrafica("Gráfica pendiente. Compila un programa con GRAFICAR para visualizarla.");
log("Interfaz lista. Backend esperado en http://localhost:8080.");
