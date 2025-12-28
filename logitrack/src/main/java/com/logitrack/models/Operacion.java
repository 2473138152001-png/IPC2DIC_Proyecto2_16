package com.logitrack.models;

import java.time.LocalDateTime;

/**
 * Historial interno (en memoria) para registrar acciones importantes.
 * No usa BD: se guarda en DataStore.historial.
 */
public class Operacion {

    private String fecha;
    private String accion;
    private String detalle;

    // Constructor vacío para JSON
    public Operacion() {
    }

    public Operacion(String accion, String detalle) {
        this.fecha = LocalDateTime.now().toString();
        this.accion = accion;
        this.detalle = detalle;
    }

    public Operacion(String fecha, String accion, String detalle) {
        this.fecha = fecha;
        this.accion = accion;
        this.detalle = detalle;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }
}
