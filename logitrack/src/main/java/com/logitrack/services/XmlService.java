package com.logitrack.services;

import com.logitrack.models.*;
import com.logitrack.repositories.DataStore;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

@Service
public class XmlService {

    // =========================================================
    // CARGA DESDE MULTIPART 
    // =========================================================
    public Map<String, Object> cargarArchivoDesdeMultipart(MultipartFile file) {

        Map<String, Object> resumen = new HashMap<>();
        List<String> errores = new ArrayList<>();

        // -------------------------
        // Validaciones básicas
        // -------------------------
        if (file == null || file.isEmpty()) {
            errores.add("No se recibió ningún archivo o viene vacío.");
            resumen.put("centros", 0);
            resumen.put("rutas", 0);
            resumen.put("mensajeros", 0);
            resumen.put("paquetes", 0);
            resumen.put("solicitudes", 0);
            resumen.put("errores", errores);
            return resumen;
        }

        // -------------------------
        // Sobreescribir memoria
        // -------------------------
        DataStore.limpiar();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(file.getInputStream());
            document.getDocumentElement().normalize();

            // -------------------------
            // Validación estructura mínima (regla de negocio)
            // -------------------------
            String raiz = document.getDocumentElement() != null
                    ? document.getDocumentElement().getNodeName()
                    : "";

            if (!"logitrack".equalsIgnoreCase(raiz)) {
                errores.add("Estructura inválida: la raíz debe ser <logitrack>.");
                return resumenFinal(errores);
            }

            NodeList conf = document.getElementsByTagName("configuracion");
            if (conf == null || conf.getLength() == 0) {
                errores.add("Estructura inválida: falta el nodo <configuracion>.");
                return resumenFinal(errores);
            }

            // carga con validaciones
            cargarDesdeDocumento(document, errores);

            // asociar listas internas (centro.paquetes y centro.mensajeros)
            asociarCentros(errores);

        } catch (Exception e) {
            errores.add("XML inválido: " + e.getMessage());
        }

        return resumenFinal(errores);
    }

    // =========================================================
    // Resumen final (siempre igual)
    // =========================================================
    private Map<String, Object> resumenFinal(List<String> errores) {
        Map<String, Object> resumen = new HashMap<>();
        resumen.put("centros", DataStore.centros.size());
        resumen.put("rutas", DataStore.rutas.size());
        resumen.put("mensajeros", DataStore.mensajeros.size());
        resumen.put("paquetes", DataStore.paquetes.size());
        resumen.put("solicitudes", DataStore.solicitudes.size());
        resumen.put("errores", errores);
        return resumen;
    }

    // =========================================================
    // MÉTODO CENTRAL DE CARGA
    // - aquí se hacen validaciones, duplicados y referencias
    // - lo malo se omite, se reporta en errores y seguimos
    // =========================================================
    private void cargarDesdeDocumento(Document document, List<String> errores) {

        // sets para detectar duplicados por id
        java.util.Set<String> idsCentros = new java.util.HashSet<>();
        java.util.Set<String> idsRutas = new java.util.HashSet<>();
        java.util.Set<String> idsMensajeros = new java.util.HashSet<>();
        java.util.Set<String> idsPaquetes = new java.util.HashSet<>();
        java.util.Set<String> idsSolicitudes = new java.util.HashSet<>();

        // -------- CENTROS --------
        NodeList listaCentros = document.getElementsByTagName("centro");
        for (int i = 0; i < listaCentros.getLength(); i++) {
            try {
                Element nodo = (Element) listaCentros.item(i);

                String id = nodo.getAttribute("id").trim();
                if (id.isEmpty()) {
                    errores.add("Centro sin id (se omitió).");
                    continue;
                }
                if (idsCentros.contains(id) || DataStore.centros.containsKey(id)) {
                    errores.add("Centro duplicado: " + id + " (se omitió).");
                    continue;
                }

                String nombre = textoSeguro(nodo, "nombre");
                String ciudad = textoSeguro(nodo, "ciudad");
                int capacidad = enteroSeguro(textoSeguro(nodo, "capacidad"));

                if (nombre.isEmpty() || ciudad.isEmpty()) {
                    errores.add("Centro " + id + ": nombre/ciudad faltante (se omitió).");
                    continue;
                }
                if (capacidad <= 0) {
                    errores.add("Centro " + id + ": capacidad inválida (se omitió).");
                    continue;
                }

                Centro centro = new Centro(id, nombre, ciudad, capacidad);
                DataStore.centros.put(centro.getId(), centro);
                idsCentros.add(id);

            } catch (Exception ex) {
                errores.add("Centro: error leyendo un nodo (se omitió).");
            }
        }

        // -------- RUTAS --------
        NodeList listaRutas = document.getElementsByTagName("ruta");
        for (int i = 0; i < listaRutas.getLength(); i++) {
            try {
                Element nodo = (Element) listaRutas.item(i);

                String id = nodo.getAttribute("id").trim();
                String origen = nodo.getAttribute("origen").trim();
                String destino = nodo.getAttribute("destino").trim();
                int distancia = enteroSeguro(nodo.getAttribute("distancia").trim());

                if (id.isEmpty()) {
                    errores.add("Ruta sin id (se omitió).");
                    continue;
                }
                if (idsRutas.contains(id) || DataStore.rutas.containsKey(id)) {
                    errores.add("Ruta duplicada: " + id + " (se omitió).");
                    continue;
                }
                if (origen.isEmpty() || destino.isEmpty()) {
                    errores.add("Ruta " + id + ": origen/destino faltante (se omitió).");
                    continue;
                }
                if (distancia <= 0) {
                    errores.add("Ruta " + id + ": distancia inválida (se omitió).");
                    continue;
                }

                // referencia: centros deben existir
                if (!DataStore.centros.containsKey(origen) || !DataStore.centros.containsKey(destino)) {
                    errores.add("Ruta " + id + ": origen/destino no existe (se omitió).");
                    continue;
                }

                Ruta ruta = new Ruta(id, origen, destino, distancia);
                DataStore.rutas.put(ruta.getId(), ruta);
                idsRutas.add(id);

            } catch (Exception ex) {
                errores.add("Ruta: error leyendo un nodo (se omitió).");
            }
        }

        // -------- MENSAJEROS --------
        NodeList listaMensajeros = document.getElementsByTagName("mensajero");
        for (int i = 0; i < listaMensajeros.getLength(); i++) {
            try {
                Element nodo = (Element) listaMensajeros.item(i);

                String id = nodo.getAttribute("id").trim();
                String nombre = nodo.getAttribute("nombre").trim();
                int capacidad = enteroSeguro(nodo.getAttribute("capacidad").trim());
                String centro = nodo.getAttribute("centro").trim();

                if (id.isEmpty()) {
                    errores.add("Mensajero sin id (se omitió).");
                    continue;
                }
                if (idsMensajeros.contains(id) || DataStore.mensajeros.containsKey(id)) {
                    errores.add("Mensajero duplicado: " + id + " (se omitió).");
                    continue;
                }
                if (nombre.isEmpty()) {
                    errores.add("Mensajero " + id + ": nombre faltante (se omitió).");
                    continue;
                }
                if (capacidad <= 0) {
                    errores.add("Mensajero " + id + ": capacidad inválida (se omitió).");
                    continue;
                }
                if (centro.isEmpty() || !DataStore.centros.containsKey(centro)) {
                    errores.add("Mensajero " + id + ": centro no existe (se omitió).");
                    continue;
                }

                Mensajero mensajero = new Mensajero(id, nombre, capacidad, centro);
                DataStore.mensajeros.put(mensajero.getId(), mensajero);
                idsMensajeros.add(id);

            } catch (Exception ex) {
                errores.add("Mensajero: error leyendo un nodo (se omitió).");
            }
        }

        // -------- PAQUETES --------
        NodeList listaPaquetes = document.getElementsByTagName("paquete");
        for (int i = 0; i < listaPaquetes.getLength(); i++) {
            try {
                Element nodo = (Element) listaPaquetes.item(i);

                String id = nodo.getAttribute("id").trim();
                String cliente = nodo.getAttribute("cliente").trim();
                double peso = decimalSeguro(nodo.getAttribute("peso").trim());
                String destino = nodo.getAttribute("destino").trim();
                String estado = nodo.getAttribute("estado").trim();
                String centroActual = nodo.getAttribute("centroActual").trim();

                if (id.isEmpty()) {
                    errores.add("Paquete sin id (se omitió).");
                    continue;
                }
                if (idsPaquetes.contains(id) || DataStore.paquetes.containsKey(id)) {
                    errores.add("Paquete duplicado: " + id + " (se omitió).");
                    continue;
                }
                if (cliente.isEmpty()) {
                    errores.add("Paquete " + id + ": cliente faltante (se omitió).");
                    continue;
                }
                if (peso <= 0) {
                    errores.add("Paquete " + id + ": peso inválido (se omitió).");
                    continue;
                }
                if (destino.isEmpty() || !DataStore.centros.containsKey(destino)) {
                    errores.add("Paquete " + id + ": destino no existe (se omitió).");
                    continue;
                }
                if (centroActual.isEmpty() || !DataStore.centros.containsKey(centroActual)) {
                    errores.add("Paquete " + id + ": centroActual no existe (se omitió).");
                    continue;
                }
                if (estado.isEmpty()) {
                    estado = "PENDIENTE";
                }

                Paquete paquete = new Paquete(id, cliente, peso, destino, estado, centroActual);
                DataStore.paquetes.put(paquete.getId(), paquete);
                idsPaquetes.add(id);

            } catch (Exception ex) {
                errores.add("Paquete: error leyendo un nodo (se omitió).");
            }
        }

        // -------- SOLICITUDES --------
        NodeList listaSolicitudes = document.getElementsByTagName("solicitud");
        for (int i = 0; i < listaSolicitudes.getLength(); i++) {
            try {
                Element nodo = (Element) listaSolicitudes.item(i);

                String id = nodo.getAttribute("id").trim();
                String tipo = nodo.getAttribute("tipo").trim();
                String paqueteId = nodo.getAttribute("paquete").trim();
                int prioridad = enteroSeguro(nodo.getAttribute("prioridad").trim());

                if (id.isEmpty()) {
                    errores.add("Solicitud sin id (se omitió).");
                    continue;
                }
                if (idsSolicitudes.contains(id)) {
                    errores.add("Solicitud duplicada: " + id + " (se omitió).");
                    continue;
                }
                if (tipo.isEmpty()) {
                    errores.add("Solicitud " + id + ": tipo faltante (se omitió).");
                    continue;
                }
                if (paqueteId.isEmpty() || !DataStore.paquetes.containsKey(paqueteId)) {
                    errores.add("Solicitud " + id + ": paquete no existe (se omitió).");
                    continue;
                }
                if (prioridad <= 0) {
                    errores.add("Solicitud " + id + ": prioridad inválida (se omitió).");
                    continue;
                }

                Solicitud solicitud = new Solicitud(id, tipo, paqueteId, prioridad, "Pendiente");
                DataStore.solicitudes.add(solicitud);
                idsSolicitudes.add(id);

            } catch (Exception ex) {
                errores.add("Solicitud: error leyendo un nodo (se omitió).");
            }
        }
    }

    // =========================================================
    // ASOCIAR PAQUETES Y MENSAJEROS A CENTROS
    // - limpia listas internas primero para no duplicar
    // =========================================================
    private void asociarCentros(List<String> errores) {

        // ✅ CLAVE: si recargás, no queremos duplicar paquetes/mensajeros dentro del centro
        for (Centro c : DataStore.centros.values()) {
            c.getPaquetes().clear();
            c.getMensajeros().clear();
        }

        for (Paquete p : DataStore.paquetes.values()) {
            Centro centro = DataStore.centros.get(p.getCentroActual());
            if (centro != null) {
                centro.getPaquetes().add(p);
            } else {
                errores.add("Paquete " + p.getId()
                        + ": centroActual no existe al asociar (se omitió asociación).");
            }
        }

        for (Mensajero m : DataStore.mensajeros.values()) {
            Centro centro = DataStore.centros.get(m.getCentroActual());
            if (centro != null) {
                centro.getMensajeros().add(m);
            } else {
                errores.add("Mensajero " + m.getId()
                        + ": centro no existe al asociar (se omitió asociación).");
            }
        }
    }

    // =========================================================
    // HELPERS (para no botar el programa por tags o números malos)
    // =========================================================
    private String textoSeguro(Element nodo, String tag) {
        NodeList nl = nodo.getElementsByTagName(tag);
        if (nl == null || nl.getLength() == 0) return "";
        if (nl.item(0) == null) return "";
        return nl.item(0).getTextContent().trim();
    }

    private int enteroSeguro(String s) {
        if (s == null) return 0;
        s = s.trim();
        if (s.isEmpty()) return 0;
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return 0;
        }
    }

    private double decimalSeguro(String s) {
        if (s == null) return 0;
        s = s.trim();
        if (s.isEmpty()) return 0;
        try {
            return Double.parseDouble(s);
        } catch (Exception e) {
            return 0;
        }
    }
}
