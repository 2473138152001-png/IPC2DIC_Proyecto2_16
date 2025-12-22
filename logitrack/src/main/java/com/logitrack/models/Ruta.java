package com.logitrack.models;

public class Ruta {

    private String id;
    private String origen;
    private String destino;
    private int distancia;

    public Ruta(String id, String origen, String destino, int distancia) {
        this.id = id;
        this.origen = origen;
        this.destino = destino;
        this.distancia = distancia;
    }

    public String getId() {
        return id;
    }

    public String getOrigen() {
        return origen;
    }

    public String getDestino() {
        return destino;
    }
}
