// ruta base del backend
export const API_BASE = "http://localhost:8080";

async function manejarRespuesta(resp) {
  const tipo = resp.headers.get("content-type") || "";

  if (!resp.ok) {
    let detalle = "";

    try {
      if (tipo.includes("application/json")) {
        const json = await resp.json();
        detalle = json.message || JSON.stringify(json);
      } else {
        detalle = await resp.text();
      }
    } catch (e) {
      detalle = "Error desconocido";
    }

    throw new Error("HTTP " + resp.status + ": " + detalle);
  }

  // devuelve json o texto
  if (tipo.includes("application/json")) {
    return await resp.json();
  }

  return await resp.text();
}

// -------------------------
// importar archivo XML
// -------------------------
export async function importarXML(file) {
  const form = new FormData();
  form.append("file", file);

  const resp = await fetch(API_BASE + "/api/importar", {
    method: "POST",
    body: form,
  });

  return await manejarRespuesta(resp);
}

// -------------------------
// centros
// -------------------------
export async function getCentros() {
  const resp = await fetch(API_BASE + "/api/centros");
  return await manejarRespuesta(resp);
}

export async function getCentroPorId(id) {
  const resp = await fetch(API_BASE + "/api/centros/" + encodeURIComponent(id));
  return await manejarRespuesta(resp);
}

export async function getPaquetesPorCentro(id) {
  const resp = await fetch(API_BASE + "/api/centros/" + encodeURIComponent(id) + "/paquetes");
  return await manejarRespuesta(resp);
}

export async function getMensajerosPorCentro(id) {
  const resp = await fetch(API_BASE + "/api/centros/" + encodeURIComponent(id) + "/mensajeros");
  return await manejarRespuesta(resp);
}

// -------------------------
// rutas
// -------------------------
export async function getRutas() {
  const resp = await fetch(API_BASE + "/api/rutas");
  return await manejarRespuesta(resp);
}

export async function crearRuta(data) {
  const resp = await fetch(API_BASE + "/api/rutas", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data),
  });

  return await manejarRespuesta(resp);
}

export async function actualizarRuta(id, data) {
  const resp = await fetch(API_BASE + "/api/rutas/" + encodeURIComponent(id), {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data),
  });

  return await manejarRespuesta(resp);
}

export async function eliminarRuta(id) {
  const resp = await fetch(API_BASE + "/api/rutas/" + encodeURIComponent(id), {
    method: "DELETE",
  });

  return await manejarRespuesta(resp);
}

// -------------------------
// mensajeros
// -------------------------
export async function getMensajeros() {
  const resp = await fetch(API_BASE + "/api/mensajeros");
  return await manejarRespuesta(resp);
}

export async function crearMensajero(data) {
  const resp = await fetch(API_BASE + "/api/mensajeros", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data),
  });

  return await manejarRespuesta(resp);
}

// tu backend recibe String -> text/plain
export async function cambiarEstadoMensajero(id, estado) {
  const resp = await fetch(API_BASE + "/api/mensajeros/" + encodeURIComponent(id) + "/estado", {
    method: "PUT",
    headers: { "Content-Type": "text/plain" },
    body: estado,
  });

  return await manejarRespuesta(resp);
}

// tu backend recibe String -> text/plain
export async function cambiarCentroMensajero(id, centroId) {
  const resp = await fetch(API_BASE + "/api/mensajeros/" + encodeURIComponent(id) + "/centro", {
    method: "PUT",
    headers: { "Content-Type": "text/plain" },
    body: centroId,
  });

  return await manejarRespuesta(resp);
}

// -------------------------
// paquetes
// -------------------------
export async function getPaquetes() {
  const resp = await fetch(API_BASE + "/api/paquetes");
  return await manejarRespuesta(resp);
}

export async function crearPaquete(data) {
  const resp = await fetch(API_BASE + "/api/paquetes", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data),
  });

  return await manejarRespuesta(resp);
}

export async function actualizarPaquete(id, data) {
  const resp = await fetch(API_BASE + "/api/paquetes/" + encodeURIComponent(id), {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data),
  });

  return await manejarRespuesta(resp);
}

export async function eliminarPaquete(id) {
  const resp = await fetch(API_BASE + "/api/paquetes/" + encodeURIComponent(id), {
    method: "DELETE",
  });

  return await manejarRespuesta(resp);
}

// -------------------------
// solicitudes
// -------------------------
export async function getSolicitudes() {
  const resp = await fetch(API_BASE + "/api/solicitudes");
  return await manejarRespuesta(resp);
}

export async function crearSolicitud(data) {
  const resp = await fetch(API_BASE + "/api/solicitudes", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data),
  });

  return await manejarRespuesta(resp);
}

export async function eliminarSolicitud(id) {
  const resp = await fetch(API_BASE + "/api/solicitudes/" + encodeURIComponent(id), {
    method: "DELETE",
  });

  return await manejarRespuesta(resp);
}

// procesa la solicitud con mayor prioridad
export async function procesarTopSolicitud() {
  const resp = await fetch(API_BASE + "/api/solicitudes/procesar", {
    method: "POST",
  });

  return await manejarRespuesta(resp);
}

// procesa N solicitudes
export async function procesarNSolicitudes(n) {
  const resp = await fetch(API_BASE + "/api/solicitudes/procesar/" + n, {
    method: "POST",
  });

  return await manejarRespuesta(resp);
}

// -------------------------
// envios (asignación directa + estado)
// -------------------------
export async function asignarEnvioDirecto(paqueteId, mensajeroId) {
  const resp = await fetch(API_BASE + "/api/envios/asignar", {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ paqueteId, mensajeroId }),
  });

  return await manejarRespuesta(resp);
}

export async function cambiarEstadoEnvio(paqueteId, estado) {
  const resp = await fetch(API_BASE + "/api/envios/" + encodeURIComponent(paqueteId) + "/estado", {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ estado }),
  });

  return await manejarRespuesta(resp);
}

// -------------------------
// XML de salida
// -------------------------
export async function getXmlSalida() {
  const resp = await fetch(API_BASE + "/api/xml/salida");
  return await manejarRespuesta(resp); // texto XML
}
