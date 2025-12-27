import React, { useState } from "react";
import { getMensajeros } from "../api/api";

/*
 * Componente Mensajeros
 * Función: consumir GET /api/mensajeros
 * y mostrar la lista de mensajeros del sistema
 */
function Mensajeros(props) {

  // -------- estados --------
  const [listaMensajeros, setListaMensajeros] = useState([]);
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
