package com.logitrack;

import com.logitrack.models.Solicitud;
import com.logitrack.services.ProcesamientoService;
import com.logitrack.services.XmlService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PruebaPaso4 implements CommandLineRunner {

    @Override
    public void run(String... args) {

        System.out.println("=== PRUEBA PASO 4 ===");

        // PASO 3: cargar XML
        XmlService xmlService = new XmlService();
        xmlService.cargarArchivo("src/main/resources/logitrack.xml");

        // PASO 4: procesar solicitudes
        ProcesamientoService procesamientoService = new ProcesamientoService();
        List<Solicitud> procesadas = procesamientoService.procesarSolicitudes();

        for (Solicitud s : procesadas) {
            System.out.println(
                    "Solicitud " + s.getId()
                            + " | Prioridad: " + s.getPrioridad()
                            + " | Estado: " + s.getEstado()
            );
        }

        System.out.println("=== FIN PRUEBA PASO 4 ===");
    }
}
