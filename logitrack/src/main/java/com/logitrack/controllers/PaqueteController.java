package com.logitrack.controllers;

import com.logitrack.models.Centro;
import com.logitrack.models.Paquete;
import com.logitrack.repositories.DataStore;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;

@RestController
@RequestMapping("/api/paquetes")
public class PaqueteController {

    // GET - Listar todos
    @GetMapping
    public Collection<Paquete> listarPaquetes() {
        return DataStore.paquetes.values();
    }

    // GET - Obtener por ID
    @GetMapping("/{id}")
    public Paquete obtenerPaquete(@PathVariable String id) {
        Paquete p = DataStore.paquetes.get(id);
        if (p == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Paquete no encontrado"
            );
        }
        return p;
    }

    // POST - Crear paquete
    @PostMapping
    public Paquete crearPaquete(@RequestBody Paquete nuevo) {

        if (nuevo.getId() == null || nuevo.getId().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID requerido");
        }

        if (DataStore.paquetes.containsKey(nuevo.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ya existe un paquete con ese ID");
        }

        if (nuevo.getPeso() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El peso debe ser mayor que cero"
            );
        }

        Centro destino = DataStore.centros.get(nuevo.getDestino());
        if (destino == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Centro destino no existe"
            );
        }

        // ✅ el estado real lo controla el sistema
        nuevo.setEstado("PENDIENTE");

        // guardar
        DataStore.paquetes.put(nuevo.getId(), nuevo);

        // agregar al centro actual (si existe)
        Centro centroActual = DataStore.centros.get(nuevo.getCentroActual());
        if (centroActual != null) {
            centroActual.getPaquetes().add(nuevo);
        }

        return nuevo;
    }

    // PUT - Actualizar paquete (estado y/o centroActual)
    @PutMapping("/{id}")
    public Paquete actualizarPaquete(@PathVariable String id,
                                     @RequestBody Paquete datos) {

        Paquete p = DataStore.paquetes.get(id);
        if (p == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Paquete no encontrado"
            );
        }

        // ✅ si mandan estado, se actualiza
        if (datos.getEstado() != null && !datos.getEstado().trim().isEmpty()) {
            p.setEstado(datos.getEstado().trim());
        }

        // ✅ si mandan centroActual, validar y mover de listas
        if (datos.getCentroActual() != null && !datos.getCentroActual().trim().isEmpty()) {

            String nuevoCentroId = datos.getCentroActual().trim();
            Centro nuevoCentro = DataStore.centros.get(nuevoCentroId);

            if (nuevoCentro == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Centro actual no existe"
                );
            }

            // quitar del centro anterior
            Centro centroAnterior = DataStore.centros.get(p.getCentroActual());
            if (centroAnterior != null) {
                centroAnterior.getPaquetes().remove(p);
            }

            // agregar al nuevo centro
            nuevoCentro.getPaquetes().add(p);

            // setear
            p.setCentroActual(nuevoCentroId);
        }

        return p;
    }

    // DELETE - Eliminar paquete
    @DeleteMapping("/{id}")
    public void eliminarPaquete(@PathVariable String id) {

        Paquete p = DataStore.paquetes.get(id);
        if (p == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Paquete no encontrado"
            );
        }

        if ("EN_TRANSITO".equals(p.getEstado()) ||
                "ENTREGADO".equals(p.getEstado())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No se puede eliminar un paquete en tránsito o entregado"
            );
        }

        Centro centro = DataStore.centros.get(p.getCentroActual());
        if (centro != null) {
            centro.getPaquetes().remove(p);
        }

        DataStore.paquetes.remove(id);
    }
}
