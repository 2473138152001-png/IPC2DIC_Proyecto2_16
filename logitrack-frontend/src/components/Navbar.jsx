import React from "react";

/*
 * Componente Navbar
 * Función: mostrar el menú principal del sistema
 * y cambiar la vista activa según la opción seleccionada
 */
function Navbar(props) {

  // -------- constantes --------
  const opciones = [
    { id: "importar", texto: "Importar XML" },
    { id: "centros", texto: "Centros" },
    { id: "rutas", texto: "Rutas" },
    { id: "mensajeros", texto: "Mensajeros" },
    { id: "paquetes", texto: "Paquetes" },
    { id: "solicitudes", texto: "Solicitudes" },
    { id: "envios", texto: "Envíos + XML" } // ✅ AGREGADO
  ];

  // -------- métodos --------
  function esOpcionActiva(id) {
    return props.tab === id;
  }

  function cambiarVista(id) {
    props.setTab(id);
  }

  function renderBotones() {
    const botones = [];

    for (let i = 0; i < opciones.length; i++) {
      const opcion = opciones[i];

      botones.push(
        <button
          key={opcion.id}
          className={esOpcionActiva(opcion.id) ? "active" : ""}
          onClick={() => cambiarVista(opcion.id)}
        >
          {opcion.texto}
        </button>
      );
    }

    return botones;
  }

  // -------- render --------
  return (
    <div className="nav">
      {renderBotones()}
    </div>
  );
}

export default Navbar;
