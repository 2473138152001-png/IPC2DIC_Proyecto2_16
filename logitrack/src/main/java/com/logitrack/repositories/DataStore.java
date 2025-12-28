package com.logitrack.repositories;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import com.logitrack.models.Centro;
import com.logitrack.models.Mensajero;
import com.logitrack.models.Operacion;
import com.logitrack.models.Paquete;
import com.logitrack.models.Ruta;
import com.logitrack.models.Solicitud;

public class DataStore {

    // Almacenes en memoria
    public static Map<String, Centro> centros = new HashMap<>();
    public static Map<String, Ruta> rutas = new HashMap<>();
    public static Map<String, Mensajero> mensajeros = new HashMap<>();
    public static Map<String, Paquete> paquetes = new HashMap<>();

    // Cola de solicitudes ordenada por prioridad
    public static PriorityQueue<Solicitud> solicitudes =
            new PriorityQueue<>(
                    (a, b) -> Integer.compare(b.getPrioridad(), a.getPrioridad())
            );

    //Historial de solicitudes
    public static List<Solicitud> solicitudesHistorial = new ArrayList<>();

    //Historial interno de operaciones
    public static List<Operacion> historial = new ArrayList<>();

    // Limpia toda la info
    public static void limpiar() {
        centros.clear();
        rutas.clear();
        mensajeros.clear();
        paquetes.clear();
        solicitudes.clear();

        //limpiar historial
        solicitudesHistorial.clear();
        historial.clear();
    }
}
