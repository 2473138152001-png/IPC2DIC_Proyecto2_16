package com.logitrack.services;

import com.logitrack.models.*;
import com.logitrack.repositories.DataStore;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.*;

import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



@Service
public class XmlService {

    // =========================================================
    // CARGA DESDE RUTA (PRUEBAS LOCALES)
    // =========================================================
    public void cargarArchivo(String ruta) {
        try {
            File archivo = new File(ruta);

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            Document document = builder.parse(archivo);
            document.getDocumentElement().normalize();

            DataStore.limpiar();

            // -------- CENTROS --------
            NodeList listaCentros = document.getElementsByTagName("centro");
            for (int i = 0; i < listaCentros.getLength(); i++) {
                Element nodo = (Element) listaCentros.item(i);

                String id = nodo.getAttribute("id");
                String nombre = nodo.getElementsByTagName("nombre").item(0).getTextContent();
                String ciudad = nodo.getElementsByTagName("ciudad").item(0).getTextContent();
                int capacidad = Integer.parseInt(
                        nodo.getElementsByTagName("capacidad").item(0).getTextContent()
                );

                Centro centro = new Centro(id, nombre, ciudad, capacidad);
                DataStore.centros.put(id, centro);
            }

            // -------- RUTAS --------
            NodeList listaRutas = document.getElementsByTagName("ruta");
            for (int i = 0; i < listaRutas.getLength(); i++) {
                Element nodo = (Element) listaRutas.item(i);

                String id = nodo.getAttribute("id");
                String origen = nodo.getAttribute("origen");
                String destino = nodo.getAttribute("destino");
                int distancia = Integer.parseInt(nodo.getAttribute("distancia"));

                Ruta rut = new Ruta(id, origen, destino, distancia);
                DataStore.rutas.put(id, rut);
            }

            // -------- MENSAJEROS --------
            NodeList listaMensajeros = document.getElementsByTagName("mensajero");
            for (int i = 0; i < listaMensajeros.getLength(); i++) {
                Element nodo = (Element) listaMensajeros.item(i);

                String id = nodo.getAttribute("id");
                String nombre = nodo.getAttribute("nombre");
                int capacidad = Integer.parseInt(nodo.getAttribute("capacidad"));
                String centroId = nodo.getAttribute("centro");

                Mensajero mensajero = new Mensajero(id, nombre, capacidad, centroId);
                DataStore.mensajeros.put(id, mensajero);
            }

            // -------- PAQUETES --------
            NodeList listaPaquetes = document.getElementsByTagName("paquete");
            for (int i = 0; i < listaPaquetes.getLength(); i++) {
                Element nodo = (Element) listaPaquetes.item(i);

                String id = nodo.getAttribute("id");
                String cliente = nodo.getAttribute("cliente");
                double peso = Double.parseDouble(nodo.getAttribute("peso"));
                String destino = nodo.getAttribute("destino");
                String estado = nodo.getAttribute("estado");
                String centroActual = nodo.getAttribute("centroActual");

                Paquete paquete = new Paquete(id, cliente, peso, destino, estado, centroActual);
                DataStore.paquetes.put(id, paquete);
            }

            // -------- SOLICITUDES --------
            NodeList listaSolicitudes = document.getElementsByTagName("solicitud");
            for (int i = 0; i < listaSolicitudes.getLength(); i++) {
                Element nodo = (Element) listaSolicitudes.item(i);

                String id = nodo.getAttribute("id");
                String tipo = nodo.getAttribute("tipo");
                String paqueteId = nodo.getAttribute("paquete");
                int prioridad = Integer.parseInt(nodo.getAttribute("prioridad"));

                Solicitud solicitud = new Solicitud(id, tipo, paqueteId, prioridad, "Pendiente");
                DataStore.solicitudes.add(solicitud);
            }

            asociarCentros();

            System.out.println("XML cargado correctamente (ruta).");

        } catch (Exception e) {
            System.out.println("Error al leer el XML (ruta): " + e.getMessage());
        }
    }

    // =========================================================
    // CARGA DESDE MULTIPART (API)
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
    // MÉTODOS AUXILIARES SIMPLES
    // =========================================================
    private void cargarDesdeDocumento(Document document) {

        NodeList listaCentros = document.getElementsByTagName("centro");
        for (int i = 0; i < listaCentros.getLength(); i++) {
            Element nodo = (Element) listaCentros.item(i);
            Centro centro = new Centro(
                    nodo.getAttribute("id"),
                    nodo.getElementsByTagName("nombre").item(0).getTextContent(),
                    nodo.getElementsByTagName("ciudad").item(0).getTextContent(),
                    Integer.parseInt(nodo.getElementsByTagName("capacidad").item(0).getTextContent())
            );
            DataStore.centros.put(centro.getId(), centro);
        }

        NodeList listaMensajeros = document.getElementsByTagName("mensajero");
        for (int i = 0; i < listaMensajeros.getLength(); i++) {
            Element nodo = (Element) listaMensajeros.item(i);
            Mensajero m = new Mensajero(
                    nodo.getAttribute("id"),
                    nodo.getAttribute("nombre"),
                    Integer.parseInt(nodo.getAttribute("capacidad")),
                    nodo.getAttribute("centro")
            );
            DataStore.mensajeros.put(m.getId(), m);
        }

        NodeList listaPaquetes = document.getElementsByTagName("paquete");
        for (int i = 0; i < listaPaquetes.getLength(); i++) {
            Element nodo = (Element) listaPaquetes.item(i);
            Paquete p = new Paquete(
                    nodo.getAttribute("id"),
                    nodo.getAttribute("cliente"),
                    Double.parseDouble(nodo.getAttribute("peso")),
                    nodo.getAttribute("destino"),
                    nodo.getAttribute("estado"),
                    nodo.getAttribute("centroActual")
            );
            DataStore.paquetes.put(p.getId(), p);
        }

        NodeList listaSolicitudes = document.getElementsByTagName("solicitud");
        for (int i = 0; i < listaSolicitudes.getLength(); i++) {
            Element nodo = (Element) listaSolicitudes.item(i);
            Solicitud s = new Solicitud(
                    nodo.getAttribute("id"),
                    nodo.getAttribute("tipo"),
                    nodo.getAttribute("paquete"),
                    Integer.parseInt(nodo.getAttribute("prioridad")),
                    "Pendiente"
            );
            DataStore.solicitudes.add(s);
        }
    }

    private void asociarCentros() {
        for (Paquete p : DataStore.paquetes.values()) {
            Centro c = DataStore.centros.get(p.getCentroActual());
            if (c != null) c.getPaquetes().add(p);
        }

        for (Mensajero m : DataStore.mensajeros.values()) {
            Centro c = DataStore.centros.get(m.getCentroActual());
            if (c != null) c.getMensajeros().add(m);
        }
    }
}
