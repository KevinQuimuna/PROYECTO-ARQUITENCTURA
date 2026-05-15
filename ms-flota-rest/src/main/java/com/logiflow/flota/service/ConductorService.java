package com.logiflow.flota.service;

import com.logiflow.flota.api.dto.ConductorRequest;
import com.logiflow.flota.api.dto.ConductorResponse;
import com.logiflow.flota.domain.Conductor;
import com.logiflow.flota.domain.EstadoConductor;
import com.logiflow.flota.domain.Vehiculo;
import com.logiflow.flota.repo.ConductorRepository;
import com.logiflow.flota.repo.VehiculoRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ConductorService {

    private final ConductorRepository conductorRepository;
    private final VehiculoRepository vehiculoRepository;

    public ConductorService(ConductorRepository conductorRepository, VehiculoRepository vehiculoRepository) {
        this.conductorRepository = conductorRepository;
        this.vehiculoRepository = vehiculoRepository;
    }

    @Transactional(readOnly = true)
    public List<ConductorResponse> listar() {
        return conductorRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ConductorResponse obtener(String id) {
        return toResponse(buscar(id));
    }

    @Transactional
    public ConductorResponse crear(ConductorRequest req) {
        if (conductorRepository.existsByLicencia(req.licencia())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Licencia ya registrada");
        }
        Conductor c = new Conductor();
        aplicar(c, req);
        return toResponse(conductorRepository.save(c));
    }

    @Transactional
    public ConductorResponse actualizar(String id, ConductorRequest req) {
        Conductor c = buscar(id);
        if (!c.getLicencia().equalsIgnoreCase(req.licencia())
                && conductorRepository.existsByLicencia(req.licencia())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Licencia ya registrada");
        }
        aplicar(c, req);
        return toResponse(conductorRepository.save(c));
    }

    @Transactional
    public void eliminar(String id) {
        conductorRepository.delete(buscar(id));
    }

    @Transactional(readOnly = true)
    public List<ConductorResponse> disponiblesParaRuteo() {
        return conductorRepository.findByEstado(EstadoConductor.DISPONIBLE).stream()
                .map(this::toResponse)
                .toList();
    }

    private void aplicar(Conductor c, ConductorRequest req) {
        c.setNombreCompleto(req.nombreCompleto());
        c.setLicencia(req.licencia());
        c.setEstado(req.estado());
        if (req.vehiculoId() == null) {
            c.setVehiculoAsignado(null);
        } else {
            Vehiculo v = vehiculoRepository
                    .findById(req.vehiculoId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vehículo no existe"));
            c.setVehiculoAsignado(v);
        }
    }

    private Conductor buscar(String id) {
        return conductorRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conductor no encontrado"));
    }

    private ConductorResponse toResponse(Conductor c) {
        Vehiculo v = c.getVehiculoAsignado();
        return new ConductorResponse(
                c.getId(),
                c.getNombreCompleto(),
                c.getLicencia(),
                v != null ? v.getId() : null,
                v != null ? v.getMatricula() : null,
                c.getEstado());
    }
}
