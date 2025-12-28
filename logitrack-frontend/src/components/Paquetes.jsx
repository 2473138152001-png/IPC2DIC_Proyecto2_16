import React, { useState } from "react";
import {
  getPaquetes,
  crearPaquete,
  actualizarPaquete,
  eliminarPaquete
} from "../api/api";

/*
 * Componente Paquetes
 * Función:
 * - GET /api/paquetes (listar)
 * - POST /api/paquetes (crear)
 * - PUT /api/paquetes/{id} (actualizar)
 * - DELETE /api/paquetes/{id} (eliminar)
 */
function Paquetes(props) {

  // -------- estados --------
  const [listaPaquetes, setListaPaquetes] = useState([]);
  const [estaCargando, setEstaCargando] = useState(false);

  // crear
  const [idPaquete, setIdPaquete] = useState("");
  const [clientePaquete, setClientePaquete] = useState("");
  const [pesoPaquete, setPesoPaquete] = useState("");
  const [destinoPaquete, setDestinoPaquete] = useState("");
  const [centroActualPaquete, setCentroActualPaquete] = useState("");

  // actualizar (estado + centroActual)
  const [idPaqueteActualizar, setIdPaqueteActualizar] = useState("");
  const [estadoPaqueteActualizar, setEstadoPaqueteActualizar] = useState("PENDIENTE");
  const [centroActualActualizar, setCentroActualActualizar] = useState("");

  // eliminar
  const [idPaqueteEliminar, setIdPaqueteEliminar] = useState("");

  // -------- métodos --------
  function limpiarMensajes() {
    props.setError("");
    props.setOk("");
  }

  function procesarRespuesta(respuesta) {
    if (Array.isArray(respuesta)) {
      return respuesta;
    }

    if (respuesta && respuesta.paquetes) {
      return respuesta.paquetes;
    }

    return [];
  }

  async function cargarPaquetes() {
    limpiarMensajes();
    setEstaCargando(true);

    try {
      const respuesta = await getPaquetes();
      const lista = procesarRespuesta(respuesta);

      setListaPaquetes(lista);
      props.setOk("Paquetes cargados correctamente.");

    } catch (error) {
      props.setError(error.message);
      setListaPaquetes([]);

    } finally {
      setEstaCargando(false);
    }
  }

  async function crearNuevoPaquete() {
    limpiarMensajes();

    try {
      const data = {
        id: idPaquete.trim(),
        cliente: clientePaquete.trim(),
        peso: Number(pesoPaquete),
        destino: destinoPaquete.trim(),
        centroActual: centroActualPaquete.trim()
        // estado no se manda, tu backend lo pone PENDIENTE
      };

      await crearPaquete(data);

      props.setOk("Paquete creado correctamente.");

      setIdPaquete("");
      setClientePaquete("");
      setPesoPaquete("");
      setDestinoPaquete("");
      setCentroActualPaquete("");

      await cargarPaquetes();

    } catch (error) {
      props.setError(error.message);
    }
  }

  async function actualizarPaqueteExistente() {
    limpiarMensajes();

    try {
      // mandamos solo lo necesario: estado y centroActual
      const data = {
        estado: estadoPaqueteActualizar,
        centroActual: centroActualActualizar.trim()
      };

      await actualizarPaquete(idPaqueteActualizar.trim(), data);

      props.setOk("Paquete actualizado correctamente.");

      setIdPaqueteActualizar("");
      setCentroActualActualizar("");

      await cargarPaquetes();

    } catch (error) {
      props.setError(error.message);
    }
  }

  async function eliminarPaqueteExistente() {
    limpiarMensajes();

    try {
      await eliminarPaquete(idPaqueteEliminar.trim());

      props.setOk("Paquete eliminado correctamente.");
      setIdPaqueteEliminar("");

      await cargarPaquetes();

    } catch (error) {
      props.setError(error.message);
    }
  }

  // -------- render --------
  return (
    <div className="card">
      <h3 className="title">Paquetes</h3>

      <div className="row">
        <button
          className="btn"
          onClick={cargarPaquetes}
          disabled={estaCargando}
        >
          {estaCargando ? "Cargando..." : "Cargar paquetes"}
        </button>
      </div>

      <div className="hr"></div>

      <h4 style={{ margin: "0 0 8px 0" }}>Crear paquete</h4>
      <div className="row">
        <input
          className="input"
          placeholder="ID (P001)"
          value={idPaquete}
          onChange={(e) => setIdPaquete(e.target.value)}
        />
        <input
          className="input"
          placeholder="Cliente"
          value={clientePaquete}
          onChange={(e) => setClientePaquete(e.target.value)}
        />
        <input
          className="input"
          placeholder="Peso (10)"
          value={pesoPaquete}
          onChange={(e) => setPesoPaquete(e.target.value)}
        />
        <input
          className="input"
          placeholder="Destino (C002)"
          value={destinoPaquete}
          onChange={(e) => setDestinoPaquete(e.target.value)}
        />
        <input
          className="input"
          placeholder="Centro actual (C001)"
          value={centroActualPaquete}
          onChange={(e) => setCentroActualPaquete(e.target.value)}
        />

        <button
          className="btn"
          onClick={crearNuevoPaquete}
          disabled={!idPaquete || !clientePaquete || !pesoPaquete || !destinoPaquete || !centroActualPaquete}
        >
          Crear
        </button>
      </div>

      <div className="hr"></div>

      <h4 style={{ margin: "0 0 8px 0" }}>Actualizar paquete</h4>
      <div className="row">
        <input
          className="input"
          placeholder="ID (P001)"
          value={idPaqueteActualizar}
          onChange={(e) => setIdPaqueteActualizar(e.target.value)}
        />

        <select
          className="select"
          value={estadoPaqueteActualizar}
          onChange={(e) => setEstadoPaqueteActualizar(e.target.value)}
        >
          <option value="PENDIENTE">PENDIENTE</option>
          <option value="EN TRANSITO">EN_TRANSITO</option>
          <option value="ENTREGADO">ENTREGADO</option>
        </select>

        <input
          className="input"
          placeholder="Centro actual (C002)"
          value={centroActualActualizar}
          onChange={(e) => setCentroActualActualizar(e.target.value)}
        />

        <button
          className="btn2"
          onClick={actualizarPaqueteExistente}
          disabled={!idPaqueteActualizar || !centroActualActualizar}
        >
          Actualizar
        </button>
      </div>

      <div className="hr"></div>

      <h4 style={{ margin: "0 0 8px 0" }}>Eliminar paquete</h4>
      <div className="row">
        <input
          className="input"
          placeholder="ID (P001)"
          value={idPaqueteEliminar}
          onChange={(e) => setIdPaqueteEliminar(e.target.value)}
        />

        <button
          className="btn2"
          onClick={eliminarPaqueteExistente}
          disabled={!idPaqueteEliminar}
        >
          Eliminar
        </button>
      </div>

      <div className="hr"></div>

      {listaPaquetes.length === 0 && (
        <p className="small">
          No hay paquetes cargados o todavía no se ha consultado al backend.
        </p>
      )}

      {listaPaquetes.length > 0 && (
        <pre>
          {JSON.stringify(listaPaquetes, null, 2)}
        </pre>
      )}
    </div>
  );
}

export default Paquetes;
