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

        nuevo.setEstado("PENDIENTE");
        DataStore.paquetes.put(nuevo.getId(), nuevo);

        Centro centroActual = DataStore.centros.get(nuevo.getCentroActual());
        if (centroActual != null) {
            centroActual.getPaquetes().add(nuevo);
        }

        return nuevo;
    }


    // PUT - Actualizar paquete

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

        if (datos.getPeso() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Peso inválido"
            );
        }

        p.setEstado(datos.getEstado());
        p.setCentroActual(datos.getCentroActual());

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

        if (p.getEstado().equals("EN_TRANSITO") ||
                p.getEstado().equals("ENTREGADO")) {
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
