package com.logitrack.models;

public class Paquete {

    private String id;
    private String cliente;
    private double peso;
    private String destino;
    private String estado;
    private String centroActual;
    private String ultimaActualizacion;

    // ✅ Constructor vacío para JSON
    public Paquete() {
    }

    public Paquete(String id, String cliente, double peso, String destino, String estado, String centroActual) {
        this.id = id;
        this.cliente = cliente;
        this.peso = peso;
        this.destino = destino;
        this.estado = estado;
        this.centroActual = centroActual;
    }

    public String getId() {
        return id;
    }

    // ✅ setters necesarios para JSON
    public void setId(String id) {
        this.id = id;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
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

    public String getUltimaActualizacion() {
        return ultimaActualizacion;
    }

    public void setUltimaActualizacion(String ultimaActualizacion) {
        this.ultimaActualizacion = ultimaActualizacion;
    }
}
