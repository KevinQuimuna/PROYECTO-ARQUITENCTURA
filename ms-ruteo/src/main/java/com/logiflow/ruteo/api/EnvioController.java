package com.logiflow.ruteo.api;

import com.logiflow.ruteo.application.EnvioService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class EnvioController {

    private final EnvioService envios;

    public EnvioController(EnvioService envios) {
        this.envios = envios;
    }

    @GetMapping("/envios")
    public List<EnvioResponse> listar() {
        return envios.listarTodos();
    }

    @GetMapping("/envios/{id}")
    public EnvioResponse get(@PathVariable Long id) {
        return envios.obtener(id);
    }

    @GetMapping("/envios/codigo/{codigo}")
    public EnvioResponse porCodigo(@PathVariable String codigo) {
        return envios.porCodigo(codigo);
    }

    @PostMapping("/asignacion/manual")
    public EnvioResponse asignacionManual(@Valid @RequestBody AsignacionManualRequest body) {
        return envios.asignacionManual(body);
    }

    @PostMapping("/envios/{id}/iniciar-ruta")
    public EnvioResponse iniciar(@PathVariable Long id) {
        return envios.iniciarRuta(id);
    }

    @PostMapping("/envios/{id}/entregar")
    public EnvioResponse entregar(@PathVariable Long id) {
        return envios.entregar(id);
    }
}
