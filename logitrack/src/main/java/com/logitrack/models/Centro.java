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

    public String getNombre() {
        return nombre;
    }

    public String getCiudad() {
        return ciudad;
    }

    public int getCapacidad() {
        return capacidad;
    }

    // Cantidad de paquetes actuales
    public int getCargaActual() {
        return paquetes.size();
    }

    // Porcentaje de uso del centro
    public double getPorcentajeUso() {
        if (capacidad == 0) {
            return 0;
        }
        return (paquetes.size() * 100.0) / capacidad;
    }

    public List<Paquete> getPaquetes() {
        return paquetes;
    }

    public List<Mensajero> getMensajeros() {
        return mensajeros;
    }

    // Setters opcionales (útiles más adelante)
    public void setPaquetes(List<Paquete> paquetes) {
        this.paquetes = paquetes;
    }

    public void setMensajeros(List<Mensajero> mensajeros) {
        this.mensajeros = mensajeros;
    }
}
