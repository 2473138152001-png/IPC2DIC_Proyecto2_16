package com.logitrack.models;

public class Mensajero {
    private String id;
    private String nombre;
    private int capacidad;
    private double cargaActual;
    private String estado;
    private String centroActual;

    public Mensajero() {
    }

    public Mensajero(String id, String nombre, int capacidad, String centroActual) {
        this.id = id;
        this.nombre = nombre;
        this.capacidad = capacidad;
        this.centroActual = centroActual;
        this.cargaActual = 0;
        this.estado = "DISPONIBLE";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    public double getCargaActual() {
        return cargaActual;
    }

    public void setCargaActual(double cargaActual) {
        this.cargaActual = cargaActual;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCentroActual() {
        return centroActual;
    }

    public void setCentroActual(String centroActual) {
        this.centroActual = centroActual;
    }
}
