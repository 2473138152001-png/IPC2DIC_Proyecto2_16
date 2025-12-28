package com.logitrack.models;

public class Ruta {

    private String id;
    private String origen;
    private String destino;
    private int distancia;

    // ✅ constructor vacío (obligatorio para JSON)
    public Ruta() {
    }

    public Ruta(String id, String origen, String destino, int distancia) {
        this.id = id;
        this.origen = origen;
        this.destino = destino;
        this.distancia = distancia;
    }

    public String getId() {
        return id;
    }

    // ✅ setter faltante
    public void setId(String id) {
        this.id = id;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public int getDistancia() {
        return distancia;
    }

    public void setDistancia(int distancia) {
        this.distancia = distancia;
    }
}
