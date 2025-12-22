package com.logitrack.models;

import java.util.ArrayList;
import java.util.List;

public class Centro {

    private String id;
    private String nombre;
    private String ciudad;
    private int capacidad;

    private List<Paquete> paquetes;
    private List<Mensajero> mensajeros;

    public Centro(String id, String nombre, String ciudad, int capacidad) {
        this.id = id;
        this.nombre = nombre;
        this.ciudad = ciudad;
        this.capacidad = capacidad;
        this.paquetes = new ArrayList<>();
        this.mensajeros = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public List<Paquete> getPaquetes() {
        return paquetes;
    }

    public List<Mensajero> getMensajeros() {
        return mensajeros;
    }
}
