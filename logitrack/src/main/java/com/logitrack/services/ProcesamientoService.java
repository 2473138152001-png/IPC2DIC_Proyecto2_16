package com.logitrack.services;

import com.logitrack.models.Paquete;
import com.logitrack.models.Solicitud;
import com.logitrack.repositories.DataStore;
import com.logitrack.utils.Estado;

import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;


@Service
public class ProcesamientoService {

    public List<Solicitud> procesarSolicitudes() {

        List<Solicitud> procesadas = new ArrayList<>();
        PriorityQueue<Solicitud> cola = DataStore.solicitudes;

        while (!cola.isEmpty()) {

            Solicitud solicitud = cola.poll();

            // Marcar solicitud en proceso
            solicitud.setEstado(Estado.EN_PROCESO);

            Paquete paquete = DataStore.paquetes.get(solicitud.getPaqueteId());

            if (paquete != null) {
                // Simular envío
                paquete.setEstado(Estado.PAQ_EN_RUTA);

                // Simular entrega
                paquete.setEstado(Estado.PAQ_ENTREGADO);
            }

            // Marcar solicitud como procesada
            solicitud.setEstado(Estado.PROCESADA);
            procesadas.add(solicitud);
        }

        return procesadas;
    }
}
