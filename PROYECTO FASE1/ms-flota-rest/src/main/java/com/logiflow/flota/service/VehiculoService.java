package com.logiflow.flota.service;

import com.logiflow.flota.api.dto.VehiculoRequest;
import com.logiflow.flota.api.dto.VehiculoResponse;
import com.logiflow.flota.domain.EstadoVehiculo;
import com.logiflow.flota.domain.TipoVehiculo;
import com.logiflow.flota.domain.Vehiculo;
import com.logiflow.flota.repo.VehiculoRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class VehiculoService {

    private final VehiculoRepository vehiculoRepository;

    public VehiculoService(VehiculoRepository vehiculoRepository) {
        this.vehiculoRepository = vehiculoRepository;
    }

    @Transactional(readOnly = true)
    public List<VehiculoResponse> listar() {
        return vehiculoRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public VehiculoResponse obtener(Long id) {
        return toResponse(buscar(id));
    }

    @Transactional(readOnly = true)
    public VehiculoResponse porMatricula(String matricula) {
        return vehiculoRepository
                .findByMatriculaIgnoreCase(matricula)
                .map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehículo no encontrado"));
    }

    @Transactional
    public VehiculoResponse crear(VehiculoRequest req) {
        if (vehiculoRepository.existsByMatriculaIgnoreCase(req.matricula())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Matrícula ya registrada");
        }
        Vehiculo v = new Vehiculo();
        aplicar(v, req);
        return toResponse(vehiculoRepository.save(v));
    }

    @Transactional
    public VehiculoResponse actualizar(Long id, VehiculoRequest req) {
        Vehiculo v = buscar(id);
        if (!v.getMatricula().equalsIgnoreCase(req.matricula())
                && vehiculoRepository.existsByMatriculaIgnoreCase(req.matricula())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Matrícula ya registrada");
        }
        aplicar(v, req);
        return toResponse(vehiculoRepository.save(v));
    }

    @Transactional
    public void eliminar(Long id) {
        vehiculoRepository.delete(buscar(id));
    }

    /**
     * Consulta de disponibilidad para el contexto de Ruteo: vehículos listos para asignación.
     */
    @Transactional(readOnly = true)
    public List<VehiculoResponse> disponibles(TipoVehiculo tipo) {
        if (tipo == null) {
            return vehiculoRepository.findByEstado(EstadoVehiculo.DISPONIBLE).stream()
                    .map(this::toResponse)
                    .toList();
        }
        return vehiculoRepository.findByEstadoAndTipo(EstadoVehiculo.DISPONIBLE, tipo).stream()
                .map(this::toResponse)
                .toList();
    }

    private void aplicar(Vehiculo v, VehiculoRequest req) {
        v.setMatricula(req.matricula().trim().toUpperCase());
        v.setTipo(req.tipo());
        v.setCapacidadKg(req.capacidadKg());
        v.setAutonomiaKm(req.autonomiaKm());
        v.setEstado(req.estado());
    }

    private Vehiculo buscar(Long id) {
        return vehiculoRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehículo no encontrado"));
    }

    private VehiculoResponse toResponse(Vehiculo v) {
        return new VehiculoResponse(
                v.getId(), v.getMatricula(), v.getTipo(), v.getCapacidadKg(), v.getAutonomiaKm(), v.getEstado());
    }
}
