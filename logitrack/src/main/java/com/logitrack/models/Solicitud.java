package com.logitrack.models;

public class Solicitud {

    private String id;
    private String tipo;
    private String paqueteId;
    private int prioridad;
    private String estado;

    public Solicitud(String id, String tipo, String paqueteId, int prioridad, String estado) {
        this.id = id;
        this.tipo = tipo;
        this.paqueteId = paqueteId;
        this.prioridad = prioridad;
        this.estado = estado;
    }

    public String getId() {
        return id;
    }

    public String getTipo() {
        return tipo;
    }

    public String getPaqueteId() {
        return paqueteId;
    }

    public int getPrioridad() {
        return prioridad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
