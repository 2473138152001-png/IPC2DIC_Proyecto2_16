package com.logitrack.models;

public class Mensajero {

    private String id;
    private String nombre;
    private int capacidad;
    private String estado;
    private String centroActual;

    // Constructor simple (coincide con el XML)
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

    public String getEstado() {
        return estado;
    }

    public String getCentroActual() {
        return centroActual;
    }

    public void setCentroActual(String centroActual) {
        this.centroActual = centroActual;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
