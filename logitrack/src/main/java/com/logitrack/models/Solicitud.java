package com.logitrack.models;

public class Solicitud {

    private String id;
    private String tipo;
    private String paqueteId;
    private int prioridad;
    private String estado;

    public Solicitud(String id, String tipo, String paqueteId, int prioridad) {
        this.id = id;
        this.tipo = tipo;
        this.paqueteId = paqueteId;
        this.prioridad = prioridad;
        this.estado = "Pendiente";
    }

    public int getPrioridad() {
        return prioridad;
    }

    public String getId() {
        return id;
    }
}
