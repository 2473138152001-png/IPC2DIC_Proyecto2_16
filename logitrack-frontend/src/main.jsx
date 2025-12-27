// importa react
import React from "react";

// importa el render de react
import ReactDOM from "react-dom/client";

// importa el componente principal
import App from "./App";

// renderiza la aplicación en el div root
ReactDOM.createRoot(document.getElementById("root")).render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);
