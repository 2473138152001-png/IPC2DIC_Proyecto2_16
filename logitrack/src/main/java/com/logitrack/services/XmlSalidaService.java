package com.logitrack.services;

import com.logitrack.models.*;
import com.logitrack.repositories.DataStore;
import org.springframework.stereotype.Service;

@Service
public class XmlSalidaService {

    public String generarXmlSalida() {

        StringBuilder xml = new StringBuilder();

        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<resultadoLogitrack>\n");

        // estadistica
        xml.append("  <estadisticas>\n");
        xml.append("    <paquetesProcesados>")
                .append(DataStore.paquetes.size())
                .append("</paquetesProcesados>\n");

        xml.append("    <solicitudesAtendidas>")
                .append(DataStore.solicitudes.size())
                .append("</solicitudesAtendidas>\n");

        long mensajerosActivos = DataStore.mensajeros.values().stream()
                .filter(m -> m.getEstado().equals("EN_TRANSITO"))
                .count();

        xml.append("    <mensajerosActivos>")
                .append(mensajerosActivos)
                .append("</mensajerosActivos>\n");

        xml.append("  </estadisticas>\n");

        // centros
        xml.append("  <centros>\n");
        for (Centro c : DataStore.centros.values()) {
            xml.append("    <centro id=\"").append(c.getId()).append("\">\n");
            xml.append("      <paquetesActuales>")
                    .append(c.getPaquetes().size())
                    .append("</paquetesActuales>\n");

            long disponibles = c.getMensajeros().stream()
                    .filter(m -> m.getEstado().equals("DISPONIBLE"))
                    .count();

            xml.append("      <mensajerosDisponibles>")
                    .append(disponibles)
                    .append("</mensajerosDisponibles>\n");
            xml.append("    </centro>\n");
        }
        xml.append("  </centros>\n");

        // mensajeros
        xml.append("  <mensajeros>\n");
        for (Mensajero m : DataStore.mensajeros.values()) {
            xml.append("    <mensajero id=\"")
                    .append(m.getId())
                    .append("\" estado=\"")
                    .append(m.getEstado())
                    .append("\"/>\n");
        }
        xml.append("  </mensajeros>\n");

        //paquetes
        xml.append("  <paquetes>\n");
        for (Paquete p : DataStore.paquetes.values()) {
            xml.append("    <paquete id=\"")
                    .append(p.getId())
                    .append("\" estado=\"")
                    .append(p.getEstado())
                    .append("\" centroActual=\"")
                    .append(p.getCentroActual())
                    .append("\"/>\n");
        }
        xml.append("  </paquetes>\n");

        // solicitudes
        xml.append("  <solicitudes>\n");
        for (Solicitud s : DataStore.solicitudes) {
            xml.append("    <solicitud id=\"")
                    .append(s.getId())
                    .append("\" estado=\"")
                    .append(s.getEstado())
                    .append("\" paquete=\"")
                    .append(s.getPaqueteId())
                    .append("\"/>\n");
        }
        xml.append("  </solicitudes>\n");

        xml.append("</resultadoLogitrack>");

        return xml.toString();
    }
}
