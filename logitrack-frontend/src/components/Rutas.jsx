import React, { useState } from "react";
import { 
  getRutas,
  crearRuta,
  actualizarRuta,
  eliminarRuta
} from "../api/api";

/*
 * Componente Rutas
 * Función:
 * - GET /api/rutas (listar)
 * - POST /api/rutas (crear)
 * - PUT /api/rutas/{id} (actualizar)
 * - DELETE /api/rutas/{id} (eliminar)
 */
function Rutas(props) {

  // -------- estados --------
  const [listaRutas, setListaRutas] = useState([]);
  const [estaCargando, setEstaCargando] = useState(false);

  // crear
  const [idRuta, setIdRuta] = useState("");
  const [origenRuta, setOrigenRuta] = useState("");
  const [destinoRuta, setDestinoRuta] = useState("");
  const [distanciaRuta, setDistanciaRuta] = useState("");

  // actualizar
  const [idRutaActualizar, setIdRutaActualizar] = useState("");
  const [origenActualizar, setOrigenActualizar] = useState("");
  const [destinoActualizar, setDestinoActualizar] = useState("");
  const [distanciaActualizar, setDistanciaActualizar] = useState("");

  // eliminar
  const [idRutaEliminar, setIdRutaEliminar] = useState("");

  // -------- métodos --------
  function limpiarMensajes() {
    props.setError("");
    props.setOk("");
  }

  function procesarRespuesta(respuesta) {
    if (Array.isArray(respuesta)) {
      return respuesta;
    }

    if (respuesta && respuesta.rutas) {
      return respuesta.rutas;
    }

    return [];
  }

  async function cargarRutas() {
    limpiarMensajes();
    setEstaCargando(true);

    try {
      const respuesta = await getRutas();
      const lista = procesarRespuesta(respuesta);

      setListaRutas(lista);
      props.setOk("Rutas cargadas correctamente.");

    } catch (error) {
      props.setError(error.message);
      setListaRutas([]);

    } finally {
      setEstaCargando(false);
    }
  }

  async function crearNuevaRuta() {
    limpiarMensajes();

    try {
      const data = {
        id: idRuta.trim(),
        origen: origenRuta.trim(),
        destino: destinoRuta.trim(),
        distancia: Number(distanciaRuta)
      };

      await crearRuta(data);

      props.setOk("Ruta creada correctamente.");

      // limpiar form
      setIdRuta("");
      setOrigenRuta("");
      setDestinoRuta("");
      setDistanciaRuta("");

      await cargarRutas();

    } catch (error) {
      props.setError(error.message);
    }
  }

  async function actualizarRutaExistente() {
    limpiarMensajes();

    try {
      const data = {
        origen: origenActualizar.trim(),
        destino: destinoActualizar.trim(),
        distancia: Number(distanciaActualizar)
      };

      await actualizarRuta(idRutaActualizar.trim(), data);

      props.setOk("Ruta actualizada correctamente.");

      // limpiar form
      setIdRutaActualizar("");
      setOrigenActualizar("");
      setDestinoActualizar("");
      setDistanciaActualizar("");

      await cargarRutas();

    } catch (error) {
      props.setError(error.message);
    }
  }

  async function eliminarRutaExistente() {
    limpiarMensajes();

    try {
      await eliminarRuta(idRutaEliminar.trim());

      props.setOk("Ruta eliminada correctamente.");
      setIdRutaEliminar("");

      await cargarRutas();

    } catch (error) {
      props.setError(error.message);
    }
  }

  // -------- render --------
  return (
    <div className="card">
      <h3 className="title">Rutas</h3>

      <div className="row">
        <button
          className="btn"
          onClick={cargarRutas}
          disabled={estaCargando}
        >
          {estaCargando ? "Cargando..." : "Cargar rutas"}
        </button>
      </div>

      <div className="hr"></div>

      <h4 style={{ margin: "0 0 8px 0" }}>Crear ruta</h4>
      <div className="row">
        <input
          className="input"
          placeholder="ID (R001)"
          value={idRuta}
          onChange={(e) => setIdRuta(e.target.value)}
        />
        <input
          className="input"
          placeholder="Origen (C001)"
          value={origenRuta}
          onChange={(e) => setOrigenRuta(e.target.value)}
        />
        <input
          className="input"
          placeholder="Destino (C002)"
          value={destinoRuta}
          onChange={(e) => setDestinoRuta(e.target.value)}
        />
        <input
          className="input"
          placeholder="Distancia (205)"
          value={distanciaRuta}
          onChange={(e) => setDistanciaRuta(e.target.value)}
        />

        <button
          className="btn"
          onClick={crearNuevaRuta}
          disabled={!idRuta || !origenRuta || !destinoRuta || !distanciaRuta}
        >
          Crear
        </button>
      </div>

      <div className="hr"></div>

      <h4 style={{ margin: "0 0 8px 0" }}>Actualizar ruta</h4>
      <div className="row">
        <input
          className="input"
          placeholder="ID existente (R001)"
          value={idRutaActualizar}
          onChange={(e) => setIdRutaActualizar(e.target.value)}
        />
        <input
          className="input"
          placeholder="Nuevo origen (C001)"
          value={origenActualizar}
          onChange={(e) => setOrigenActualizar(e.target.value)}
        />
        <input
          className="input"
          placeholder="Nuevo destino (C002)"
          value={destinoActualizar}
          onChange={(e) => setDestinoActualizar(e.target.value)}
        />
        <input
          className="input"
          placeholder="Nueva distancia (210)"
          value={distanciaActualizar}
          onChange={(e) => setDistanciaActualizar(e.target.value)}
        />

        <button
          className="btn2"
          onClick={actualizarRutaExistente}
          disabled={!idRutaActualizar || !origenActualizar || !destinoActualizar || !distanciaActualizar}
        >
          Actualizar
        </button>
      </div>

      <div className="hr"></div>

      <h4 style={{ margin: "0 0 8px 0" }}>Eliminar ruta</h4>
      <div className="row">
        <input
          className="input"
          placeholder="ID (R001)"
          value={idRutaEliminar}
          onChange={(e) => setIdRutaEliminar(e.target.value)}
        />
        <button
          className="btn2"
          onClick={eliminarRutaExistente}
          disabled={!idRutaEliminar}
        >
          Eliminar
        </button>
      </div>

      <div className="hr"></div>

      {listaRutas.length === 0 && (
        <p className="small">
          No hay rutas cargadas o todavía no se ha consultado al backend.
        </p>
      )}

      {listaRutas.length > 0 && (
        <pre>
          {JSON.stringify(listaRutas, null, 2)}
        </pre>
      )}
    </div>
  );
}

export default Rutas;
