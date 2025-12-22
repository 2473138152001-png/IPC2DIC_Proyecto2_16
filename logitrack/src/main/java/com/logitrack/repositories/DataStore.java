package com.logitrack.repositories;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

import com.logitrack.models.Centro;
import com.logitrack.models.Mensajero;
import com.logitrack.models.Paquete;
import com.logitrack.models.Ruta;
import com.logitrack.models.Solicitud;

public class DataStore {

    public static Map<String, Centro> centros = new HashMap<>();
    public static Map<String, Ruta> rutas = new HashMap<>();
    public static Map<String, Mensajero> mensajeros = new HashMap<>();
    public static Map<String, Paquete> paquetes = new HashMap<>();

    public static PriorityQueue<Solicitud> solicitudes =
            new PriorityQueue<>((a, b) -> b.getPrioridad() - a.getPrioridad());

    public static void limpiar() {
        centros.clear();
        rutas.clear();
        mensajeros.clear();
        paquetes.clear();
        solicitudes.clear();//hola
    }
}
