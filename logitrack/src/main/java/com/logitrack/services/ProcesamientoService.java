package com.logitrack.services;

import com.logitrack.models.Paquete;
import com.logitrack.models.Solicitud;
import com.logitrack.repositories.DataStore;
import com.logitrack.utils.Estado;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class ProcesamientoService {
    public List<Solicitud> procesarSolicitudes(){
        List<Solicitud> procesadas = new ArrayList<>();
        PriorityQueue<Solicitud> cola = DataStore.solicitudes;

        while (!cola.isEmpty()){
            Solicitud solicitud = cola.poll();
            solicitud.setEstado(Estado.EN_PROCESO);

            Paquete paquete = DataStore.paquetes.get(solicitud.getPaqueteId());

            if(paquete != null){
                paquete.setEstado(Estado.PAQ_EN_RUTA);

                paquete.setEstado(Estado.PAQ_ENTREGADO);
            }
            solicitud.setEstado(Estado.PROCESADA);
            procesadas.add(solicitud);
        }
        return procesadas;
    }

}
