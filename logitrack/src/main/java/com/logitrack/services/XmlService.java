package com.logitrack.services;
import com.logitrack.models.*;
import com.logitrack.repositories.DataStore;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.*;

import java.io.File;

public class XmlService {
    public void cargarArchivo(String ruta) {
        try {
            File archivo = new File(ruta);

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            Document document = builder.parse(archivo);
            document.getDocumentElement().normalize();

            // 2. Limpiar memoria
            DataStore.limpiar();

            // =====================
            // 3. CARGAR CENTROS
            // =====================
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

            // =====================
            // 4. CARGAR RUTAS
            // =====================
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

            // =====================
            // 5. CARGAR MENSAJEROS
            // =====================
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

            // =====================
            // 6. CARGAR PAQUETES
            // =====================
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

            // =====================
            // 7. CARGAR SOLICITUDES
            // =====================
            NodeList listaSolicitudes = document.getElementsByTagName("solicitud");

            for (int i = 0; i < listaSolicitudes.getLength(); i++) {

                Element nodo = (Element) listaSolicitudes.item(i);

                String id = nodo.getAttribute("id");
                String tipo = nodo.getAttribute("tipo");
                String paqueteId = nodo.getAttribute("paquete");
                int prioridad = Integer.parseInt(nodo.getAttribute("prioridad"));
                String estado = nodo.getAttribute("estado");

                Solicitud solicitud = new Solicitud(id, tipo, paqueteId, prioridad, estado);
                DataStore.solicitudes.add(solicitud);
            }

            System.out.println("XML cargado correctamente.");

        } catch (Exception e) {
            System.out.println("Error al leer el XML: " + e.getMessage());
        }
    }
}
