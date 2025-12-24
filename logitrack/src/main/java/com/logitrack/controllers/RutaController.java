package com.logitrack.controllers;

import com.logitrack.models.Ruta;
import com.logitrack.repositories.DataStore;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;

@RestController
@RequestMapping("/api/rutas")

public class RutaController {

    //GET listar todas las rutas
    @GetMapping
    public Collection<Ruta> listarRutas(){
        return DataStore.rutas.values();
    }

    // GET Obtener ruta por id

    @GetMapping("/{id}")
    public Ruta obtenetRuta(@PathVariable String id){
        Ruta ruta = DataStore.rutas.get(id);
        if(ruta == null){
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Ruta no encontrada"
            );
        }
        return ruta;
    }

    //POST crear ruta

    @PostMapping
    public Ruta crearRuta(@RequestBody Ruta nuevaRuta){
        if (DataStore.rutas.containsKey(nuevaRuta.getId())){
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Ya existe una ruta con este id"
            );
        }
        for (Ruta r: DataStore.rutas.values()){
            if (r.getOrigen().equals(nuevaRuta.getOrigen()) && r.getDestino().equals(nuevaRuta.getDestino())){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Ya existe la ruta con ese origen y destino"
                );
            }
        }
        DataStore.rutas.put(nuevaRuta.getId(), nuevaRuta);
        return nuevaRuta;
    }

    //PUT actualizar ruta
    @PutMapping("/{id}")
    public Ruta actualizarRuta(@PathVariable String id, @RequestBody Ruta datos){

        Ruta ruta = DataStore.rutas.get(id);
        if(ruta == null){
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Ruta no encontrada"
            );
        }
        ruta.setDestino(datos.getOrigen());
        ruta.setDestino(datos.getDestino());
        ruta.setDistancia(datos.getDistancia());
        return ruta;
    }


    // DELETE - Eliminar ruta

    @DeleteMapping("/{id}")
    public void eliminarRuta(@PathVariable String id) {

        Ruta ruta = DataStore.rutas.get(id);
        if (ruta == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Ruta no encontrada"
            );
        }


        DataStore.rutas.remove(id);
    }

}
