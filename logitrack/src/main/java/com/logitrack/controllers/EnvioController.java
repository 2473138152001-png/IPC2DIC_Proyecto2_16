package com.logitrack.controllers;

import com.logitrack.models.Mensajero;
import com.logitrack.models.Operacion;
import com.logitrack.models.Paquete;
import com.logitrack.models.Ruta;
import com.logitrack.repositories.DataStore;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/envios")
public class EnvioController {

    // Asignación directa
    @PutMapping("/asignar")
    public String asignarMensajero(@RequestBody Map<String, String> datos) {

        String paqueteId = datos.get("paqueteId");
        String mensajeroId = datos.get("mensajeroId");

        if (paqueteId == null || paqueteId.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "paqueteId es requerido");
        }
        if (mensajeroId == null || mensajeroId.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "mensajeroId es requerido");
        }

        Paquete paquete = DataStore.paquetes.get(paqueteId);
        if (paquete == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Paquete no existe");
        }

        Mensajero mensajero = DataStore.mensajeros.get(mensajeroId);
        if (mensajero == null) 
            {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El mensajero no existe");
        }

        if (!paquete.getCentroActual().equals(mensajero.getCentroActual())) 
            {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Paquete y mensajero no están en el mismo centro");
        }

        if (!"PENDIENTE".equalsIgnoreCase(paquete.getEstado())) 
            {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Paquete No PENDIENTE");
        }

        if (!"DISPONIBLE".equalsIgnoreCase(mensajero.getEstado())) 
            {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mensaje No DISPONIBLE");
        }

        Ruta ruta = null;
        for (Ruta r : DataStore.rutas.values()) 
            {
            if (r.getOrigen().equals(paquete.getCentroActual())
                    && r.getDestino().equals(paquete.getDestino())) {
                ruta = r;
                break;
            }
        }
        if (ruta == null) 
            {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No existe ruta del centro actual al destino");
        }

        // Validar capacidad real
        double nuevaCarga = mensajero.getCargaActual() + paquete.getPeso();
        if (nuevaCarga > mensajero.getCapacidad()) 
            {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Capacidad insuficiente del mensajero");
        }

        // Cambiar estados y registrar asignacion
        mensajero.setCargaActual(nuevaCarga);
        mensajero.setEstado("EN_TRANSITO");

        paquete.setEstado("EN_TRANSITO");
        paquete.setUltimaActualizacion(LocalDateTime.now().toString());
        paquete.setMensajeroAsignadoId(mensajero.getId());
        paquete.setRutaAsignadaId(ruta.getId());

        // Saca paquetes del centro origen si existiera lista de paquetes
        if (DataStore.centros.containsKey(paquete.getCentroActual())
                && DataStore.centros.get(paquete.getCentroActual()).getPaquetes() != null) 
            {
            DataStore.centros.get(paquete.getCentroActual()).getPaquetes().removeIf(p -> p.getId().equals(paquete.getId()));
        }

        DataStore.historial.add(new Operacion(
                "ASIGNACION_DIRECTA",
                "Paquete " + paquete.getId() + " asignado a mensajero " + mensajero.getId() + " (ruta " + ruta.getId() + ")"
        ));

        return "Mensajero asignado correctamente al paquete";
    }

    // Cambiar estado del envío
    @PutMapping("/{id}/estado")
    public Paquete cambiarEstadoEnvio(@PathVariable String id,
                                      @RequestBody Map<String, String> body) {

        String nuevoEstado = body.get("estado");
        if (nuevoEstado == null || nuevoEstado.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "estado es requerido");
        }

        Paquete paquete = DataStore.paquetes.get(id);
        if (paquete == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Paquete no encontrado");
        }

        String estadoActual = paquete.getEstado();

        if ("PENDIENTE".equals(estadoActual) && "EN_TRANSITO".equals(nuevoEstado)) {
        } else if ("EN_TRANSITO".equals(estadoActual) && "ENTREGADO".equals(nuevoEstado)) {
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Transición de estado no permitida");
        }

        paquete.setEstado(nuevoEstado);
        paquete.setUltimaActualizacion(LocalDateTime.now().toString());

        // mensaje de entregado
        if ("ENTREGADO".equals(nuevoEstado)) {

            String mensajeroId = paquete.getMensajeroAsignadoId();

            if (mensajeroId != null && DataStore.mensajeros.containsKey(mensajeroId)) {
                Mensajero m = DataStore.mensajeros.get(mensajeroId);
                m.setEstado("DISPONIBLE");
                m.setCargaActual(0);
                m.setCentroActual(paquete.getDestino());
            }

            paquete.setCentroActual(paquete.getDestino());
            paquete.setMensajeroAsignadoId(null);

            if (DataStore.centros.containsKey(paquete.getCentroActual())
                    && DataStore.centros.get(paquete.getCentroActual()).getPaquetes() != null) {
                DataStore.centros.get(paquete.getCentroActual()).getPaquetes().add(paquete);
            }

            DataStore.historial.add(new Operacion(
                    "CAMBIO_ESTADO_ENVIO",
                    "Paquete " + paquete.getId() + " entregado. Mensajero liberado: " + mensajeroId
            ));

        } else {
            DataStore.historial.add(new Operacion(
                    "CAMBIO_ESTADO_ENVIO",
                    "Paquete " + paquete.getId() + " cambió a estado " + nuevoEstado
            ));
        }

        return paquete;
    }
}
