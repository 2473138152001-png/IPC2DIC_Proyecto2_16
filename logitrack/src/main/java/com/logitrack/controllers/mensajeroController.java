package com.logitrack.controllers;

import com.logitrack.models.Centro;
import com.logitrack.models.Mensajero;
import com.logitrack.repositories.DataStore;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;

@RestController
@RequestMapping("/api/mensajeros")
public class mensajeroController {


    // GET - Listar todos

    @GetMapping
    public Collection<Mensajero> listarMensajeros() {
        return DataStore.mensajeros.values();
    }


    // GET - Obtener por id

    @GetMapping("/{id}")
    public Mensajero obtenerMensajero(@PathVariable String id) {
        Mensajero m = DataStore.mensajeros.get(id);
        if (m == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Mensajero no encontrado"
            );
        }
        return m;
    }


    // POST - Crear mensajero

    @PostMapping
    public Mensajero crearMensajero(@RequestBody Mensajero nuevo) {

        if (DataStore.mensajeros.containsKey(nuevo.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Ya existe un mensajero con ese ID"
            );
        }

        Centro centro = DataStore.centros.get(nuevo.getCentroActual());
        if (centro == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Centro no existe"
            );
        }

        DataStore.mensajeros.put(nuevo.getId(), nuevo);
        centro.getMensajeros().add(nuevo);

        return nuevo;
    }


    // PUT - Cambiar estado

    @PutMapping("/{id}/estado")
    public Mensajero cambiarEstado(@PathVariable String id,
                                   @RequestBody String estado) {

        Mensajero m = DataStore.mensajeros.get(id);
        if (m == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Mensajero no encontrado"
            );
        }

        if (!estado.equals("DISPONIBLE") && !estado.equals("EN_TRANSITO")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Estado inválido"
            );
        }

        m.setEstado(estado);
        return m;
    }


    // PUT - Cambiar centro

    @PutMapping("/{id}/centro")
    public Mensajero cambiarCentro(@PathVariable String id,
                                   @RequestBody String nuevoCentroId) {

        Mensajero m = DataStore.mensajeros.get(id);
        if (m == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Mensajero no encontrado"
            );
        }

        if (m.getEstado().equals("EN_TRANSITO")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Mensajero en tránsito no puede cambiar de centro"
            );
        }

        Centro centroActual = DataStore.centros.get(m.getCentroActual());
        Centro nuevoCentro = DataStore.centros.get(nuevoCentroId);

        if (nuevoCentro == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Centro destino no existe"
            );
        }

        if (centroActual != null) {
            centroActual.getMensajeros().remove(m);
        }

        nuevoCentro.getMensajeros().add(m);
        m.setCentroActual(nuevoCentroId);

        return m;
    }
}
