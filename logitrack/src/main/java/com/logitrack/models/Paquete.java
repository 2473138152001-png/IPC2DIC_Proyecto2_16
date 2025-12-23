package com.logitrack.models;

public class Paquete {

    private String id;
    private String cliente;
    private double peso;
    private String destino;
    private String estado;
    private String centroActual;

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

    public String getCliente() {
        return cliente;
    }

    public double getPeso() {
        return peso;
    }

    public String getDestino() {
        return destino;
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
