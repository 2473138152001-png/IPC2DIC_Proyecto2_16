package com.logitrack.controllers;

import com.logitrack.models.*;
import com.logitrack.repositories.DataStore;
import com.logitrack.services.ProcesamientoService;
import com.logitrack.services.XmlService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class LogitrackController {

    @Autowired
    private XmlService xmlService;

    @Autowired
    private ProcesamientoService procesamientoService;

    // ----------------------------
    // GET - CONSULTAS
    // ----------------------------

    @GetMapping("/centros")
    public Collection<Centro> obtenerCentros() {
        return DataStore.centros.values();
    }

    @GetMapping("/centros/{id}")
    public Centro obtenerCentroPorId(@PathVariable String id) {
        Centro centro = DataStore.centros.get(id);
        if (centro == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Centro no encontrado"
            );
        }
        return centro;
    }

    @GetMapping("/centros/{id}/paquetes")
    public List<Paquete> obtenerPaquetesDeCentro(@PathVariable String id) {
        Centro centro = DataStore.centros.get(id);
        if (centro == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Centro no encontrado"
            );
        }
        return centro.getPaquetes();
    }

    @GetMapping("/centros/{id}/mensajeros")
    public List<Mensajero> obtenerMensajerosDeCentro(@PathVariable String id) {
        Centro centro = DataStore.centros.get(id);
        if (centro == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Centro no encontrado"
            );
        }
        return centro.getMensajeros();
    }

    @GetMapping("/paquetes")
    public Collection<Paquete> obtenerPaquetes() {
        return DataStore.paquetes.values();
    }

    @GetMapping("/solicitudes")
    public Collection<Solicitud> obtenerSolicitudes() {
        return DataStore.solicitudes;
    }

    // ----------------------------
    // POST - ACCIONES
    // ----------------------------

    @PostMapping("/importar")
    public Map<String, Object> importarXML(@RequestParam("file") MultipartFile file) {
        return xmlService.cargarArchivoDesdeMultipart(file);
    }

    @PostMapping("/procesar")
    public List<Solicitud> procesarSolicitudes() {
        return procesamientoService.procesarSolicitudes();
    }
}