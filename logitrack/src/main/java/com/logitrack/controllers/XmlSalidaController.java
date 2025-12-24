package com.logitrack.controllers;

import com.logitrack.services.XmlSalidaService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/xml")
public class XmlSalidaController {

    private final XmlSalidaService service;

    public XmlSalidaController(XmlSalidaService service) {
        this.service = service;
    }

    @GetMapping(value = "/salida", produces = MediaType.APPLICATION_XML_VALUE)
    public String obtenerXmlSalida() {
        return service.generarXmlSalida();
    }
}
