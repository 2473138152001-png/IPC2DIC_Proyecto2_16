package com.logitrack.models;

public class Paquete {

    private String id;
    private String cliente;
    private double peso;
    private String destino;
    private String estado;
    private String centroActual;
    private String ultimaActualizacion;
    private String mensajeroAsignadoId;
    private String rutaAsignadaId;

    // Constructor vacío para permitir deserialización JSON
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

    // Estado del paquete
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

    public String getMensajeroAsignadoId() {
        return mensajeroAsignadoId;
    }

    public void setMensajeroAsignadoId(String mensajeroAsignadoId) {
        this.mensajeroAsignadoId = mensajeroAsignadoId;
    }

    public String getRutaAsignadaId() {
        return rutaAsignadaId;
    }

    public void setRutaAsignadaId(String rutaAsignadaId) {
        this.rutaAsignadaId = rutaAsignadaId;
    }
}
