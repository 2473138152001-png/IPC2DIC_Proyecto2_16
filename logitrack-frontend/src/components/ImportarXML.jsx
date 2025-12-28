import React, { useState } from "react";
import { importarXML } from "../api/api";

/*
 * Componente ImportarXML
 * Permite cargar el archivo XML inicial y enviarlo al backend
 * para registrar los datos base del sistema.
 */
function ImportarXML(props) {

  // -------- estados --------
  const [archivoXml, setArchivoXml] = useState(null);
  const [estaCargando, setEstaCargando] = useState(false);
  const [textoRespuesta, setTextoRespuesta] = useState("");

  function limpiarMensajes() {
    props.setError("");
    props.setOk("");
    setTextoRespuesta("");
  }

  function seleccionarArchivo(evento) {
    // evento.target.files es un arreglo de archivos
    if (evento && evento.target && evento.target.files && evento.target.files.length > 0) {
      setArchivoXml(evento.target.files[0]);
    } else {
      setArchivoXml(null);
    }
  }

  function validarArchivo() {
    if (archivoXml === null) {
      props.setError("Seleccioná un archivo XML primero.");
      return false;
    }
    return true;
  }

  function formatearRespuesta(respuesta) {
    // El backend puede devolver texto o JSON
    if (typeof respuesta === "string") return respuesta;
    try {
      return JSON.stringify(respuesta, null, 2);
    } catch (e) {
      return "No se pudo convertir la respuesta a texto.";
    }
  }

  async function importar() {
    limpiarMensajes();

    if (!validarArchivo()) {
      return;
    }

    setEstaCargando(true);

    try {
      const respuesta = await importarXML(archivoXml);
      const texto = formatearRespuesta(respuesta);

      setTextoRespuesta(texto);
      props.setOk("XML importado correctamente (si el backend lo procesó sin errores).");

    } catch (error) {
      props.setError(error.message);
      setTextoRespuesta("");

    } finally {
      setEstaCargando(false);
    }
  }

  // -------- render --------
  return (
    <div className="card">
      <h3 className="title">Carga Inicial (XML)</h3>

      <p className="small">
        Subí el XML inicial. El backend debe cargar centros, rutas, mensajeros, paquetes y solicitudes.
      </p>

      <div className="hr"></div>

      <div className="row">
        <input
          className="input"
          type="file"
          accept=".xml"
          onChange={seleccionarArchivo}
        />

        <button
          className="btn"
          onClick={importar}
          disabled={estaCargando}
        >
          {estaCargando ? "Importando..." : "Importar"}
        </button>
      </div>

      {textoRespuesta !== "" && (
        <>
          <div className="hr"></div>
          <p className="small">Respuesta del backend:</p>
          <pre>{textoRespuesta}</pre>
        </>
      )}
    </div>
  );
}

export default ImportarXML;
