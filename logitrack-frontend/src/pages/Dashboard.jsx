import { useState } from "react";
import Navbar from "../components/Navbar";
import ImportarXML from "../components/ImportarXML";
import Centros from "../components/Centros";
import Rutas from "../components/Rutas";
import Mensajeros from "../components/Mensajeros";
import Paquetes from "../components/Paquetes";
import Solicitudes from "../components/Solicitudes";
import Mensajes from "../components/Mensajes";

export default function Dashboard() {
  const [tab, setTab] = useState("importar");
  const [ok, setOk] = useState("");
  const [error, setError] = useState("");

  return (
    <div className="container">
      <div className="card">
        <div className="title">LogiTrack - Frontend (React/Vite)</div>
        <div className="small">
          Panel para cargar XML, ver recursos y procesar solicitudes.
        </div>
      </div>

      <Mensajes ok={ok} error={error} />

      <Navbar tab={tab} setTab={setTab} />

      {tab === "importar" && <ImportarXML setOk={setOk} setError={setError} />}
      {tab === "centros" && <Centros setOk={setOk} setError={setError} />}
      {tab === "rutas" && <Rutas setOk={setOk} setError={setError} />}
      {tab === "mensajeros" && <Mensajeros setOk={setOk} setError={setError} />}
      {tab === "paquetes" && <Paquetes setOk={setOk} setError={setError} />}
      {tab === "solicitudes" && <Solicitudes setOk={setOk} setError={setError} />}
    </div>
  );
}
