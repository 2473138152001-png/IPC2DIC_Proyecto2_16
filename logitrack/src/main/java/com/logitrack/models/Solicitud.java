package com.logitrack.models;

public class Solicitud {

    private String id;
    private String tipo;
    private String paqueteId;
    private int prioridad;
    private String estado;
    private String motivo;

    public Solicitud() {
    }

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

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setPaqueteId(String paqueteId) {
        this.paqueteId = paqueteId;
    }

    public void setPrioridad(int prioridad) {
        this.prioridad = prioridad;
    }
}
