import React from "react";

/*
 * Componente Mensajes
 * Función: mostrar mensajes de éxito o error
 * según lo que envíe el componente padre
 */
function Mensajes(props) {

  // -------- validación --------
  function noHayMensajes() {
    if (props.error === "" && props.ok === "") {
      return true;
    }
    return false;
  }

  // -------- render --------
  if (noHayMensajes()) {
    return null;
  }

  return (
    <div className="card">
      {props.ok !== "" && (
        <div>
          {props.ok}
        </div>
      )}

      {props.error !== "" && (
        <div>
          {props.error}
        </div>
      )}
    </div>
  );
}

export default Mensajes;
