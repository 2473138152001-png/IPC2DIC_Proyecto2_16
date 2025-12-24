package com.logitrack.models;

public class Mensajero {

    private String id;
    private String nombre;
    private int capacidad;
    private String estado;
    private String centroActual;


    public Mensajero(String id, String nombre, int capacidad, String centroActual) {
        this.id = id;
        this.nombre = nombre;
        this.capacidad = capacidad;
        this.centroActual = centroActual;
        this.estado = "Disponible"; // estado por defecto
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public String getEstado() {
        return estado;
    }

    public String getCentroActual() {
        return centroActual;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public void setCentroActual(String centroActual) {
        this.centroActual = centroActual;
    }
}
