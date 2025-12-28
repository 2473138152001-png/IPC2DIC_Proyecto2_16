import React, { useState } from "react";
import {
  getMensajeros,
  crearMensajero,
  cambiarEstadoMensajero,
  cambiarCentroMensajero
} from "../api/api";

/*
 * Componente Mensajeros
 * Función:
 * - GET /api/mensajeros (listar)
 * - POST /api/mensajeros (crear)
 * - PUT /api/mensajeros/{id}/estado (cambiar estado)
 * - PUT /api/mensajeros/{id}/centro (cambiar centro)
 */
function Mensajeros(props) {

  // -------- estados --------
  const [listaMensajeros, setListaMensajeros] = useState([]);
  const [estaCargando, setEstaCargando] = useState(false);

  // crear
  const [idMensajero, setIdMensajero] = useState("");
  const [nombreMensajero, setNombreMensajero] = useState("");
  const [capacidadMensajero, setCapacidadMensajero] = useState("");
  const [centroMensajero, setCentroMensajero] = useState("");

  // cambiar estado
  const [idEstado, setIdEstado] = useState("");
  const [estadoNuevo, setEstadoNuevo] = useState("DISPONIBLE");

  // cambiar centro
  const [idCentro, setIdCentro] = useState("");
  const [centroNuevo, setCentroNuevo] = useState("");

  // -------- métodos --------
  function limpiarMensajes() {
    props.setError("");
    props.setOk("");
  }

  function procesarRespuesta(respuesta) {
    if (Array.isArray(respuesta)) {
      return respuesta;
    }

    if (respuesta && respuesta.mensajeros) {
      return respuesta.mensajeros;
    }

    return [];
  }

  async function cargarMensajeros() {
    limpiarMensajes();
    setEstaCargando(true);

    try {
      const respuesta = await getMensajeros();
      const lista = procesarRespuesta(respuesta);

      setListaMensajeros(lista);
      props.setOk("Mensajeros cargados correctamente.");

    } catch (error) {
      props.setError(error.message);
      setListaMensajeros([]);

    } finally {
      setEstaCargando(false);
    }
  }

  async function crearNuevoMensajero() {
    limpiarMensajes();

    try {
      const data = {
        id: idMensajero.trim(),
        nombre: nombreMensajero.trim(),
        capacidad: Number(capacidadMensajero),
        centroActual: centroMensajero.trim()
        // estado no es necesario mandarlo, tu backend lo pone a DISPONIBLE
      };

      await crearMensajero(data);

      props.setOk("Mensajero creado correctamente.");

      setIdMensajero("");
      setNombreMensajero("");
      setCapacidadMensajero("");
      setCentroMensajero("");

      await cargarMensajeros();

    } catch (error) {
      props.setError(error.message);
    }
  }

  async function actualizarEstado() {
    limpiarMensajes();

    try {
      await cambiarEstadoMensajero(idEstado.trim(), estadoNuevo);

      props.setOk("Estado actualizado correctamente.");
      setIdEstado("");

      await cargarMensajeros();

    } catch (error) {
      props.setError(error.message);
    }
  }

  async function reasignarCentro() {
    limpiarMensajes();

    try {
      await cambiarCentroMensajero(idCentro.trim(), centroNuevo.trim());

      props.setOk("Centro reasignado correctamente.");
      setIdCentro("");
      setCentroNuevo("");

      await cargarMensajeros();

    } catch (error) {
      props.setError(error.message);
    }
  }

  // -------- render --------
  return (
    <div className="card">
      <h3 className="title">Mensajeros</h3>

      <div className="row">
        <button
          className="btn"
          onClick={cargarMensajeros}
          disabled={estaCargando}
        >
          {estaCargando ? "Cargando..." : "Cargar mensajeros"}
        </button>
      </div>

      <div className="hr"></div>

      <h4 style={{ margin: "0 0 8px 0" }}>Crear mensajero</h4>
      <div className="row">
        <input
          className="input"
          placeholder="ID (M001)"
          value={idMensajero}
          onChange={(e) => setIdMensajero(e.target.value)}
        />
        <input
          className="input"
          placeholder="Nombre"
          value={nombreMensajero}
          onChange={(e) => setNombreMensajero(e.target.value)}
        />
        <input
          className="input"
          placeholder="Capacidad (10)"
          value={capacidadMensajero}
          onChange={(e) => setCapacidadMensajero(e.target.value)}
        />
        <input
          className="input"
          placeholder="Centro (C001)"
          value={centroMensajero}
          onChange={(e) => setCentroMensajero(e.target.value)}
        />

        <button
          className="btn"
          onClick={crearNuevoMensajero}
          disabled={!idMensajero || !nombreMensajero || !capacidadMensajero || !centroMensajero}
        >
          Crear
        </button>
      </div>

      <div className="hr"></div>

      <h4 style={{ margin: "0 0 8px 0" }}>Cambiar estado</h4>
      <div className="row">
        <input
          className="input"
          placeholder="ID (M001)"
          value={idEstado}
          onChange={(e) => setIdEstado(e.target.value)}
        />

        <select
          className="select"
          value={estadoNuevo}
          onChange={(e) => setEstadoNuevo(e.target.value)}
        >
          <option value="DISPONIBLE">DISPONIBLE</option>
          <option value="EN TRANSITO">EN_TRANSITO</option>
        </select>

        <button
          className="btn2"
          onClick={actualizarEstado}
          disabled={!idEstado}
        >
          Actualizar estado
        </button>
      </div>

      <div className="hr"></div>

      <h4 style={{ margin: "0 0 8px 0" }}>Cambiar centro</h4>
      <div className="row">
        <input
          className="input"
          placeholder="ID (M001)"
          value={idCentro}
          onChange={(e) => setIdCentro(e.target.value)}
        />
        <input
          className="input"
          placeholder="Nuevo centro (C002)"
          value={centroNuevo}
          onChange={(e) => setCentroNuevo(e.target.value)}
        />

        <button
          className="btn2"
          onClick={reasignarCentro}
          disabled={!idCentro || !centroNuevo}
        >
          Cambiar centro
        </button>
      </div>

      <div className="hr"></div>

      {listaMensajeros.length === 0 && (
        <p className="small">
          No hay mensajeros cargados o todavía no se ha consultado al backend.
        </p>
      )}

      {listaMensajeros.length > 0 && (
        <pre>
          {JSON.stringify(listaMensajeros, null, 2)}
        </pre>
      )}
    </div>
  );
}

export default Mensajeros;
