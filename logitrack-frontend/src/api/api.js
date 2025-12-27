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


// importar archivo XML
export async function importarXML(file) {
  const form = new FormData();

  form.append("file", file);

  const resp = await fetch(API_BASE + "/api/importar", {
    method: "POST",
    body: form,
  });

  return await manejarRespuesta(resp);
}

// centros
export async function getCentros() {
  const resp = await fetch(API_BASE + "/api/centros");
  return await manejarRespuesta(resp);
}

// rutas
export async function getRutas() {
  const resp = await fetch(API_BASE + "/api/rutas");
  return await manejarRespuesta(resp);
}


// mensajeros
export async function getMensajeros() {
  const resp = await fetch(API_BASE + "/api/mensajeros");
  return await manejarRespuesta(resp);
}

// paquetes
export async function getPaquetes() {
  const resp = await fetch(API_BASE + "/api/paquetes");
  return await manejarRespuesta(resp);
}

// las solicitudes
export async function getSolicitudes() {
  const resp = await fetch(API_BASE + "/api/solicitudes");
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
  const resp = await fetch(
    API_BASE + "/api/solicitudes/procesar/" + n,
    { method: "POST" }
  );

  return await manejarRespuesta(resp);
}
