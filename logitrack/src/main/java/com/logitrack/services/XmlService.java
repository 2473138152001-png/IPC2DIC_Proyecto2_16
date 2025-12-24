package com.logitrack.services;

import com.logitrack.models.*;
import com.logitrack.repositories.DataStore;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

@Service
public class XmlService {



    // =========================================================
    // CARGA DESDE MULTIPART (API /api/importar)
    // =========================================================
    public Map<String, Object> cargarArchivoDesdeMultipart(MultipartFile file) {

        Map<String, Object> resumen = new HashMap<>();
        List<String> errores = new ArrayList<>();

        DataStore.limpiar();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(file.getInputStream());
            document.getDocumentElement().normalize();

            cargarDesdeDocumento(document);
            asociarCentros();

        } catch (Exception e) {
            errores.add("XML inválido: " + e.getMessage());
        }

        resumen.put("centros", DataStore.centros.size());
        resumen.put("rutas", DataStore.rutas.size());
        resumen.put("mensajeros", DataStore.mensajeros.size());
        resumen.put("paquetes", DataStore.paquetes.size());
        resumen.put("solicitudes", DataStore.solicitudes.size());
        resumen.put("errores", errores);

        return resumen;
    }

    // =========================================================
    // MÉTODO CENTRAL DE CARGA (TODO EL XML)
    // =========================================================
    private void cargarDesdeDocumento(Document document) {

        // -------- CENTROS --------
        NodeList listaCentros = document.getElementsByTagName("centro");
        for (int i = 0; i < listaCentros.getLength(); i++) {
            Element nodo = (Element) listaCentros.item(i);

            Centro centro = new Centro(
                    nodo.getAttribute("id"),
                    nodo.getElementsByTagName("nombre").item(0).getTextContent(),
                    nodo.getElementsByTagName("ciudad").item(0).getTextContent(),
                    Integer.parseInt(
                            nodo.getElementsByTagName("capacidad").item(0).getTextContent()
                    )
            );

            DataStore.centros.put(centro.getId(), centro);
        }

        // -------- RUTAS --------
        NodeList listaRutas = document.getElementsByTagName("ruta");
        for (int i = 0; i < listaRutas.getLength(); i++) {
            Element nodo = (Element) listaRutas.item(i);

            Ruta ruta = new Ruta(
                    nodo.getAttribute("id"),
                    nodo.getAttribute("origen"),
                    nodo.getAttribute("destino"),
                    Integer.parseInt(nodo.getAttribute("distancia"))
            );

            DataStore.rutas.put(ruta.getId(), ruta);
        }

        // -------- MENSAJEROS --------
        NodeList listaMensajeros = document.getElementsByTagName("mensajero");
        for (int i = 0; i < listaMensajeros.getLength(); i++) {
            Element nodo = (Element) listaMensajeros.item(i);

            Mensajero mensajero = new Mensajero(
                    nodo.getAttribute("id"),
                    nodo.getAttribute("nombre"),
                    Integer.parseInt(nodo.getAttribute("capacidad")),
                    nodo.getAttribute("centro")
            );

            DataStore.mensajeros.put(mensajero.getId(), mensajero);
        }

        // -------- PAQUETES --------
        NodeList listaPaquetes = document.getElementsByTagName("paquete");
        for (int i = 0; i < listaPaquetes.getLength(); i++) {
            Element nodo = (Element) listaPaquetes.item(i);

            Paquete paquete = new Paquete(
                    nodo.getAttribute("id"),
                    nodo.getAttribute("cliente"),
                    Double.parseDouble(nodo.getAttribute("peso")),
                    nodo.getAttribute("destino"),
                    nodo.getAttribute("estado"),
                    nodo.getAttribute("centroActual")
            );

            DataStore.paquetes.put(paquete.getId(), paquete);
        }

        // -------- SOLICITUDES --------
        NodeList listaSolicitudes = document.getElementsByTagName("solicitud");
        for (int i = 0; i < listaSolicitudes.getLength(); i++) {
            Element nodo = (Element) listaSolicitudes.item(i);

            Solicitud solicitud = new Solicitud(
                    nodo.getAttribute("id"),
                    nodo.getAttribute("tipo"),
                    nodo.getAttribute("paquete"),
                    Integer.parseInt(nodo.getAttribute("prioridad")),
                    "Pendiente"
            );

            DataStore.solicitudes.add(solicitud);
        }
    }

    // =========================================================
    // ASOCIAR PAQUETES Y MENSAJEROS A CENTROS
    // =========================================================
    private void asociarCentros() {

        for (Paquete p : DataStore.paquetes.values()) {
            Centro centro = DataStore.centros.get(p.getCentroActual());
            if (centro != null) {
                centro.getPaquetes().add(p);
            }
        }

        for (Mensajero m : DataStore.mensajeros.values()) {
            Centro centro = DataStore.centros.get(m.getCentroActual());
            if (centro != null) {
                centro.getMensajeros().add(m);
            }
        }
    }
}
