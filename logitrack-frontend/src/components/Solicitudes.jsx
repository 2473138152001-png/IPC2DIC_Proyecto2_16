import React, { useState } from "react";
import {
  getSolicitudes,
  procesarTopSolicitud,
  procesarNSolicitudes
} from "../api/api";

/*
 * Componente Solicitudes
 * Función: manejar la cola de prioridad de solicitudes
 * - Consultar la cola
 * - Procesar la solicitud más prioritaria
 * - Procesar N solicitudes
 */
function Solicitudes(props) {

  // -------- estados --------
  const [listaSolicitudes, setListaSolicitudes] = useState([]);
  const [estaCargando, setEstaCargando] = useState(false);
  const [cantidadN, setCantidadN] = useState("3");
  const [textoRespuesta, setTextoRespuesta] = useState("");

  // -------- métodos auxiliares --------
  function limpiarMensajes() {
    props.setError("");
    props.setOk("");
    setTextoRespuesta("");
  }

  function procesarRespuestaLista(respuesta) {
    if (Array.isArray(respuesta)) {
      return respuesta;
    }

    if (respuesta && respuesta.solicitudes) {
      return respuesta.solicitudes;
    }

    return [];
  }

  function formatearRespuesta(respuesta) {
    if (typeof respuesta === "string") {
      return respuesta;
    }

    try {
      return JSON.stringify(respuesta, null, 2);
    } catch (e) {
      return "No se pudo formatear la respuesta del backend.";
    }
  }

  function validarCantidadN() {
    const valor = parseInt(cantidadN, 10);

    if (Number.isNaN(valor) || valor <= 0) {
      props.setError("N debe ser un número mayor que 0.");
      return null;
    }

    return valor;
  }

  // -------- acciones --------
  async function cargarSolicitudes() {
    limpiarMensajes();
    setEstaCargando(true);

    try {
      const respuesta = await getSolicitudes();
      const lista = procesarRespuestaLista(respuesta);

      setListaSolicitudes(lista);
      props.setOk("Solicitudes cargadas correctamente.");

    } catch (error) {
      props.setError(error.message);
      setListaSolicitudes([]);

    } finally {
      setEstaCargando(false);
    }
  }

  async function procesarSolicitudTop() {
    limpiarMensajes();
    setEstaCargando(true);

    try {
      const respuesta = await procesarTopSolicitud();
      const texto = formatearRespuesta(respuesta);

      setTextoRespuesta(texto);
      props.setOk("Procesada la solicitud más prioritaria.");

    } catch (error) {
      props.setError(error.message);

    } finally {
      setEstaCargando(false);
    }
  }

  async function procesarSolicitudesN() {
    limpiarMensajes();

    const valorN = validarCantidadN();
    if (valorN === null) {
      return;
    }

    setEstaCargando(true);

    try {
      const respuesta = await procesarNSolicitudes(valorN);
      const texto = formatearRespuesta(respuesta);

      setTextoRespuesta(texto);
      props.setOk("Procesadas " + valorN + " solicitudes.");

    } catch (error) {
      props.setError(error.message);

    } finally {
      setEstaCargando(false);
    }
  }

  // -------- render --------
  return (
    <div className="card">
      <h3 className="title">Solicitudes (Cola de Prioridad)</h3>

      <div className="row">
        <button
          className="btn"
          onClick={cargarSolicitudes}
          disabled={estaCargando}
        >
          {estaCargando ? "Cargando..." : "Ver cola"}
        </button>

        <button
          className="btn2"
          onClick={procesarSolicitudTop}
          disabled={estaCargando}
        >
          Procesar top
        </button>

        <input
          className="input"
          style={{ width: 90 }}
          value={cantidadN}
          onChange={(e) => setCantidadN(e.target.value)}
          placeholder="N"
        />

        <button
          className="btn2"
          onClick={procesarSolicitudesN}
          disabled={estaCargando}
        >
          Procesar N
        </button>
      </div>

      <div className="hr"></div>

      {listaSolicitudes.length === 0 && (
        <p className="small">
          No hay solicitudes cargadas o todavía no se ha consultado al backend.
        </p>
      )}

      {listaSolicitudes.length > 0 && (
        <pre>
          {JSON.stringify(listaSolicitudes, null, 2)}
        </pre>
      )}

      {textoRespuesta !== "" && (
        <>
          <div className="hr"></div>
          <p className="small">Respuesta del procesamiento:</p>
          <pre>{textoRespuesta}</pre>
        </>
      )}
    </div>
  );
}

export default Solicitudes;