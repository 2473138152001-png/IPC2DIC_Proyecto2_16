package com.logitrack.controllers;

import com.logitrack.models.Mensajero;
import com.logitrack.models.Paquete;
import com.logitrack.repositories.DataStore;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.time.LocalDateTime;
@RestController
@RequestMapping("/api/envios")
public class EnvioController {


    // PUT - Asignación directa

    @PutMapping("/asignar")
    public String asignarMensajero(@RequestBody Map<String, String> datos) {

        String paqueteId = datos.get("paqueteId");
        String mensajeroId = datos.get("mensajeroId");

        Paquete paquete = DataStore.paquetes.get(paqueteId);
        if (paquete == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El paquete no existe"
            );
        }

        Mensajero mensajero = DataStore.mensajeros.get(mensajeroId);
        if (mensajero == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El mensajero no existe"
            );
        }

        if (!paquete.getCentroActual().equals(mensajero.getCentroActual())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Paquete y mensajero no están en el mismo centro"
            );
        }

        // Cambiar estados
        paquete.setEstado("EN_TRANSITO");
        mensajero.setEstado("EN_TRANSITO");

        return "Mensajero asignado correctamente al paquete";
    }
    // PUT - Cambiar estado del envío
    // ------------------------------------
    @PutMapping("/{id}/estado")
    public Paquete cambiarEstadoEnvio(@PathVariable String id,
                                      @RequestBody Map<String, String> body) {

        String nuevoEstado = body.get("estado");

        Paquete paquete = DataStore.paquetes.get(id);
        if (paquete == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Paquete no encontrado"
            );
        }

        String estadoActual = paquete.getEstado();

        // Validar transiciones
        if (estadoActual.equals("PENDIENTE") && nuevoEstado.equals("EN_TRANSITO")) {
            // válido
        } else if (estadoActual.equals("EN_TRANSITO") && nuevoEstado.equals("ENTREGADO")) {
            // válido
        } else {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Transición de estado no permitida"
            );
        }

        // Cambiar estado
        paquete.setEstado(nuevoEstado);
        paquete.setUltimaActualizacion(LocalDateTime.now().toString());

        // Si se entrega, liberar mensajero
        if (nuevoEstado.equals("ENTREGADO")) {
            for (Mensajero m : DataStore.mensajeros.values()) {
                if (m.getEstado().equals("EN_TRANSITO")
                        && m.getCentroActual().equals(paquete.getCentroActual())) {
                    m.setEstado("DISPONIBLE");
                    break;
                }
            }
        }

        return paquete;
    }

}
