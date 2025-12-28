package com.logitrack.controllers;

import com.logitrack.models.Mensajero;
import com.logitrack.models.Operacion;
import com.logitrack.models.Paquete;
import com.logitrack.models.Ruta;
import com.logitrack.models.Solicitud;
import com.logitrack.repositories.DataStore;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/solicitudes")
public class SolicitudController {

    // GET - Ver cola de solicitudes (ordenada)
    @GetMapping
    public List<Solicitud> listarSolicitudes() {
        List<Solicitud> copia = new ArrayList<>(DataStore.solicitudes);
        copia.sort((a, b) -> Integer.compare(b.getPrioridad(), a.getPrioridad()));
        return copia;
    }

    //Crear solicitud
    @PostMapping
    public Solicitud crearSolicitud(@RequestBody Solicitud nueva) {

        if (nueva == null || nueva.getId() == null || nueva.getId().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La solicitud debe tener id");
        }

        if (nueva.getPaqueteId() == null || nueva.getPaqueteId().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La solicitud debe tener paqueteId");
        }

        Paquete paquete = DataStore.paquetes.get(nueva.getPaqueteId());
        if (paquete == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El paquete no existe");
        }

        if (!"PENDIENTE".equalsIgnoreCase(paquete.getEstado())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El paquete no está pendiente");
        }

        // Evitar duplicados por id en historial
        for (Solicitud s : DataStore.solicitudesHistorial) {
            if (s.getId() != null && s.getId().equalsIgnoreCase(nueva.getId())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Solicitud duplicada");
            }
        }

        nueva.setEstado("PENDIENTE");
        nueva.setMotivo(null);

        DataStore.solicitudes.add(nueva);
        DataStore.solicitudesHistorial.add(nueva);

        DataStore.historial.add(new Operacion(
                "CREAR SOLICITUD",
                "Solicitud " + nueva.getId() + " creada para paquete " + nueva.getPaqueteId()
        ));

        return nueva;
    }

    //Eliminar solicitud (solo si está en cola)
    @DeleteMapping("/{id}")
    public void eliminarSolicitud(@PathVariable String id) {

        Solicitud encontrada = null;

        for (Solicitud s : DataStore.solicitudes) {
            if (s.getId().equals(id)) {
                encontrada = s;
                break;
            }
        }

        if (encontrada == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Solicitud no encontrada"
            );
        }

        DataStore.solicitudes.remove(encontrada);

        DataStore.historial.add(new Operacion(
                "ELIMINAR SOLICITUD",
                "Se eliminó de la cola la solicitud " + id
        ));
    }

    //Procesar 1 solicitud
    @PostMapping("/procesar")
    public Solicitud procesarUna() {
        List<Solicitud> res = procesarInterno(1);
        if (res.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No hay solicitudes en cola");
        }
        return res.get(0);
    }

    // Procesar N solicitudes
    @PostMapping("/procesar/{n}")
    public List<Solicitud> procesarVarias(@PathVariable int n) {
        if (n <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "N debe ser mayor a 0");
        }
        return procesarInterno(n);
    }

    // Lógica interna de procesamiento
    private List<Solicitud> procesarInterno(int cantidad) {

        List<Solicitud> resultado = new ArrayList<>();

        if (DataStore.solicitudes.isEmpty()) {
            return resultado;
        }

        //Sacar hasta N solicitudes por prioridad
        List<Solicitud> lote = new ArrayList<>();
        for (int i = 0; i < cantidad; i++) {
            if (DataStore.solicitudes.isEmpty()) {
                break;
            }
            lote.add(DataStore.solicitudes.poll());
        }

        // Agrupar por ruta (origen->destino)
        Map<String, List<Solicitud>> porRuta = new LinkedHashMap<>();
        for (Solicitud s : lote) {
            Paquete p = DataStore.paquetes.get(s.getPaqueteId());
            String key;
            if (p == null) {
                key = "PAQUETE NO EXISTE";
            } else {
                key = p.getCentroActual() + "->" + p.getDestino();
            }
            porRuta.computeIfAbsent(key, k -> new ArrayList<>()).add(s);
        }

        // Procesar por grupo
        for (Map.Entry<String, List<Solicitud>> entry : porRuta.entrySet()) {

            String rutaKey = entry.getKey();
            List<Solicitud> solicitudesRuta = entry.getValue();

            // Paquete inexistente
            if ("PAQUETE NO EXISTE".equals(rutaKey)) {
                for (Solicitud s : solicitudesRuta) {
                    marcarRechazada(s, "El paquete no existe");
                    resultado.add(s);
                }
                continue;
            }

            String origen = rutaKey.split("->")[0];
            String destino = rutaKey.split("->")[1];

            Ruta ruta = buscarRuta(origen, destino);
            if (ruta == null) {
                for (Solicitud s : solicitudesRuta) {
                    marcarRechazada(s, "No existe ruta de " + origen + " a " + destino);
                    resultado.add(s);
                }
                continue;
            }

            Mensajero mensajero = buscarMensajeroDisponibleEnCentro(origen);
            if (mensajero == null) {
                for (Solicitud s : solicitudesRuta) {
                    marcarRechazada(s, "No hay mensajeros disponibles en el centro " + origen);
                    resultado.add(s);
                }
                continue;
            }

            // Intenta las asignaciones de todas las solicitudes de la ruta
            for (Solicitud s : solicitudesRuta) {

                Paquete p = DataStore.paquetes.get(s.getPaqueteId());
                if (p == null) {
                    marcarRechazada(s, "El paquete no existe");
                    resultado.add(s);
                    continue;
                }

                if (!"PENDIENTE".equalsIgnoreCase(p.getEstado())) {
                    marcarRechazada(s, "El paquete no está PENDIENTE");
                    resultado.add(s);
                    continue;
                }

                // Validar capacidad real del mensajero
                double nuevaCarga = mensajero.getCargaActual() + p.getPeso();
                if (nuevaCarga > mensajero.getCapacidad()) {
                    marcarRechazada(s, "Capacidad insuficiente del mensajero " + mensajero.getId());
                    resultado.add(s);
                    continue;
                }

                // Procesar y asignar
                mensajero.setCargaActual(nuevaCarga);
                mensajero.setEstado("ENTRANSITO");

                p.setEstado("EN TRANSITO");
                p.setUltimaActualizacion(LocalDateTime.now().toString());
                p.setMensajeroAsignadoId(mensajero.getId());
                p.setRutaAsignadaId(ruta.getId());

                // Sacar paquete del centro origen
                if (DataStore.centros.containsKey(origen) && DataStore.centros.get(origen).getPaquetes() != null) {
                    DataStore.centros.get(origen).getPaquetes().removeIf(px -> px.getId().equals(p.getId()));
                }

                s.setEstado("ATENDIDA");
                s.setMotivo("OK");

                DataStore.historial.add(new Operacion(
                        "PROCESAR SOLICITUD",
                        "Solicitud " + s.getId()
                                + " atendida: paquete " + p.getId()
                                + " -> mensajero " + mensajero.getId()
                                + " (ruta " + ruta.getId() + ")"
                ));

                resultado.add(s);
            }
        }

        return resultado;
    }

    private void marcarRechazada(Solicitud s, String motivo) {
        s.setEstado("RECHAZADA");
        s.setMotivo(motivo);

        DataStore.historial.add(new Operacion(
                "PROCESAR SOLICITUD",
                "Solicitud " + s.getId() + " rechazada: " + motivo
        ));
    }

    private Ruta buscarRuta(String origen, String destino) {
        for (Ruta r : DataStore.rutas.values()) {
            if (origen.equals(r.getOrigen()) && destino.equals(r.getDestino())) {
                return r;
            }
        }
        return null;
    }

    private Mensajero buscarMensajeroDisponibleEnCentro(String centroId) {
        for (Mensajero m : DataStore.mensajeros.values()) {
            if ("DISPONIBLE".equalsIgnoreCase(m.getEstado())
                    && centroId.equals(m.getCentroActual())) {
                return m;
            }
        }
        return null;
    }
}
