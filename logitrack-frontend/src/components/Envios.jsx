import React, { useState } from "react";
import {
  asignarEnvioDirecto,
  cambiarEstadoEnvio,
  getXmlSalida
} from "../api/api";

/*
 * Componente Envios
 * Función:
 * - Asignación directa (PUT /api/envios/asignar)
 * - Cambiar estado de envío (PUT /api/envios/{paqueteId}/estado)
 * - Generar XML de salida (GET /api/xml/salida)
 */
function Envios(props) {

  // -------- estados --------
  const [estaCargando, setEstaCargando] = useState(false);

  // asignación directa
  const [idPaqueteAsignar, setIdPaqueteAsignar] = useState("");
  const [idMensajeroAsignar, setIdMensajeroAsignar] = useState("");

  // cambiar estado
  const [idPaqueteEstado, setIdPaqueteEstado] = useState("");
  const [estadoNuevo, setEstadoNuevo] = useState("EN_TRANSITO");

  // xml salida
  const [xmlSalida, setXmlSalida] = useState("");

  // -------- métodos --------
  function limpiarMensajes() {
    props.setError("");
    props.setOk("");
  }

  function limpiarXml() {
    setXmlSalida("");
  }

  function formatearTexto(valor) {
    if (typeof valor === "string") return valor;

    try {
      return JSON.stringify(valor, null, 2);
    } catch (e) {
      return String(valor);
    }
  }

  // -------- acciones --------
  async function asignarDirecto() {
    limpiarMensajes();
    limpiarXml();
    setEstaCargando(true);

    try {
      const respuesta = await asignarEnvioDirecto(
        idPaqueteAsignar.trim(),
        idMensajeroAsignar.trim()
      );

      props.setOk("Asignación directa realizada correctamente.");
      setXmlSalida(formatearTexto(respuesta));

      setIdPaqueteAsignar("");
      setIdMensajeroAsignar("");

    } catch (error) {
      props.setError(error.message);

    } finally {
      setEstaCargando(false);
    }
  }

  async function cambiarEstado() {
    limpiarMensajes();
    limpiarXml();
    setEstaCargando(true);

    try {
      const respuesta = await cambiarEstadoEnvio(
        idPaqueteEstado.trim(),
        estadoNuevo
      );

      props.setOk("Estado del envío actualizado correctamente.");
      setXmlSalida(formatearTexto(respuesta));

      setIdPaqueteEstado("");

    } catch (error) {
      props.setError(error.message);

    } finally {
      setEstaCargando(false);
    }
  }

  async function generarXmlSalida() {
    limpiarMensajes();
    setEstaCargando(true);

    try {
      const xml = await getXmlSalida();
      setXmlSalida(String(xml));
      props.setOk("XML de salida generado correctamente.");

    } catch (error) {
      props.setError(error.message);
      setXmlSalida("");

    } finally {
      setEstaCargando(false);
    }
  }

  function descargarXml() {
    limpiarMensajes();

    if (!xmlSalida || xmlSalida.trim() === "") {
      props.setError("No hay XML para descargar. Primero genera el XML.");
      return;
    }

    const blob = new Blob([xmlSalida], { type: "application/xml" });
    const url = URL.createObjectURL(blob);

    const a = document.createElement("a");
    a.href = url;
    a.download = "resultadoLogitrack.xml";
    a.click();

    URL.revokeObjectURL(url);
    props.setOk("Descarga iniciada.");
  }

  // -------- render --------
  return (
    <div className="card">
      <h3 className="title">Envíos + XML de salida</h3>

      <div className="hr"></div>

      <h4 style={{ margin: "0 0 8px 0" }}>Asignación directa</h4>
      <div className="row">
        <input
          className="input"
          placeholder="Paquete ID (P001)"
          value={idPaqueteAsignar}
          onChange={(e) => setIdPaqueteAsignar(e.target.value)}
        />

        <input
          className="input"
          placeholder="Mensajero ID (M001)"
          value={idMensajeroAsignar}
          onChange={(e) => setIdMensajeroAsignar(e.target.value)}
        />

        <button
          className="btn"
          onClick={asignarDirecto}
          disabled={estaCargando || !idPaqueteAsignar || !idMensajeroAsignar}
        >
          {estaCargando ? "Procesando..." : "Asignar"}
        </button>
      </div>

      <div className="hr"></div>

      <h4 style={{ margin: "0 0 8px 0" }}>Cambiar estado del envío</h4>
      <div className="row">
        <input
          className="input"
          placeholder="Paquete ID (P001)"
          value={idPaqueteEstado}
          onChange={(e) => setIdPaqueteEstado(e.target.value)}
        />

        <select
          className="select"
          value={estadoNuevo}
          onChange={(e) => setEstadoNuevo(e.target.value)}
        >
          <option value="EN_TRANSITO">EN_TRANSITO</option>
          <option value="ENTREGADO">ENTREGADO</option>
        </select>

        <button
          className="btn2"
          onClick={cambiarEstado}
          disabled={estaCargando || !idPaqueteEstado}
        >
          {estaCargando ? "Procesando..." : "Actualizar"}
        </button>
      </div>

      <div className="hr"></div>

      <h4 style={{ margin: "0 0 8px 0" }}>XML de salida</h4>
      <div className="row">
        <button
          className="btn"
          onClick={generarXmlSalida}
          disabled={estaCargando}
        >
          {estaCargando ? "Generando..." : "Generar XML"}
        </button>

        <button
          className="btn2"
          onClick={descargarXml}
          disabled={!xmlSalida}
        >
          Descargar XML
        </button>
      </div>

      {xmlSalida && (
        <>
          <div className="hr"></div>
          <p className="small">Respuesta/XML:</p>
          <pre>{xmlSalida}</pre>
        </>
      )}
    </div>
  );
}

export default Envios;
