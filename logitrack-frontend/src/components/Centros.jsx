import React, { useState } from "react";
import { getCentros } from "../api/api";

/*
 * Componente Centros
 * Función: consumir el endpoint GET /api/centros
 * y mostrar la información en pantalla
 */
function Centros(props) {


  const [listaCentros, setListaCentros] = useState([]);
  const [estaCargando, setEstaCargando] = useState(false);

  async function cargarCentros() {
    props.setError("");
    props.setOk("");
    setEstaCargando(true);

    try {
      const respuesta = await getCentros();

      // el backend puede devolver arreglo directo o un objeto
      if (Array.isArray(respuesta)) {
        setListaCentros(respuesta);
      } else if (respuesta && respuesta.centros) {
        setListaCentros(respuesta.centros);
      } else {
        setListaCentros([]);
      }

      props.setOk("Centros cargados correctamente.");

    } catch (error) {
      props.setError(error.message);
      setListaCentros([]);

    } finally {
      setEstaCargando(false);
    }
  }

  // render
  return (
    <div className="card">
      <h3 className="title">Centros de Distribución</h3>

      <div className="row">
        <button
          className="btn"
          onClick={cargarCentros}
          disabled={estaCargando}
        >
          {estaCargando ? "Cargando..." : "Cargar centros"}
        </button>
      </div>

      <div className="hr"></div>

      {listaCentros.length === 0 && (
        <p className="small">
          No hay centros cargados o todavía no se ha consultado al backend.
        </p>
      )}

      {listaCentros.length > 0 && (
        <pre>
          {JSON.stringify(listaCentros, null, 2)}
        </pre>
      )}
    </div>
  );
}

export default Centros;
