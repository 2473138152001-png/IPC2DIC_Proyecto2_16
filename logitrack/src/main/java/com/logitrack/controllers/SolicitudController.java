package com.logitrack.controllers;

import com.logitrack.models.*;
import com.logitrack.repositories.DataStore;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@RestController
@RequestMapping("/api/solicitudes")
public class SolicitudController {


    // GET - Ver cola de solicitudes

    @GetMapping
    public Collection<Solicitud> listarSolicitudes() {
        return DataStore.solicitudes;
    }


    // POST - Crear solicitud

    @PostMapping
    public Solicitud crearSolicitud(@RequestBody Solicitud nueva) {

        Paquete paquete = DataStore.paquetes.get(nueva.getPaqueteId());
        if (paquete == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El paquete no existe"
            );
        }

        if (!paquete.getEstado().equals("PENDIENTE")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El paquete no está pendiente"
            );
        }

        nueva.setEstado("PENDIENTE");
        DataStore.solicitudes.add(nueva);

        return nueva;
    }


    // DELETE - Eliminar solicitud

    @DeleteMapping("/{id}")
    public void eliminarSolicitud(@PathVariable String id) {

        Solicitud encontrada = null;

        for (Solicitud s : DataStore.solicitudes) {
            if (s.getId().equals(id)) {
                encontrada = s;
                break;
            }
        }

        if (encontrada == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Solicitud no encontrada"
            );
        }

        DataStore.solicitudes.remove(encontrada);
    }


    // POST - Procesar 1 solicitud

    @PostMapping("/procesar")
    public Solicitud procesarUna() {
        return procesarInterno(1).get(0);
    }


    // POST - Procesar N solicitudes

    @PostMapping("/procesar/{n}")
    public List<Solicitud> procesarVarias(@PathVariable int n) {
        return procesarInterno(n);
    }


    // Lógica interna de procesamiento

    private List<Solicitud> procesarInterno(int cantidad) {

        List<Solicitud> resultado = new ArrayList<>();

        for (int i = 0; i < cantidad; i++) {

            if (DataStore.solicitudes.isEmpty()) {
                break;
            }

            Solicitud s = DataStore.solicitudes.poll();
            Paquete p = DataStore.paquetes.get(s.getPaqueteId());

            Mensajero mensajeroDisponible = null;
            for (Mensajero m : DataStore.mensajeros.values()) {
                if (m.getEstado().equals("DISPONIBLE")) {
                    mensajeroDisponible = m;
                    break;
                }
            }

            if (p == null || mensajeroDisponible == null) {
                s.setEstado("RECHAZADA");
                resultado.add(s);
                continue;
            }

            // Validar ruta
            boolean rutaExiste = false;
            for (Ruta r : DataStore.rutas.values()) {
                if (r.getOrigen().equals(p.getCentroActual())
                        && r.getDestino().equals(p.getDestino())) {
                    rutaExiste = true;
                    break;
                }
            }

            if (!rutaExiste) {
                s.setEstado("RECHAZADA");
                resultado.add(s);
                continue;
            }

            // PROCESAR
            mensajeroDisponible.setEstado("EN_TRANSITO");
            p.setEstado("EN_TRANSITO");
            s.setEstado("PROCESADA");

            resultado.add(s);
        }

        return resultado;
    }
}
