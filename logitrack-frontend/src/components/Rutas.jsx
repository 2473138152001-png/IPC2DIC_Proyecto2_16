import React, { useState } from "react";
import { getRutas } from "../api/api";

/*
 * Componente Rutas
 * Función: consumir GET /api/rutas
 * y mostrar la lista de rutas registradas en el sistema
 */
function Rutas(props) {

  // -------- estados --------
  const [listaRutas, setListaRutas] = useState([]);
  const [estaCargando, setEstaCargando] = useState(false);

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
