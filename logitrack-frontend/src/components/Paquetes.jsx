import React, { useState } from "react";
import { getPaquetes } from "../api/api";

/*
 * Componente Paquetes
 * Función: consumir GET /api/paquetes
 * y mostrar la lista de paquetes del sistema
 */
function Paquetes(props) {

  // -------- estados --------
  const [listaPaquetes, setListaPaquetes] = useState([]);
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
