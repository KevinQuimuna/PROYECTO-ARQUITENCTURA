package com.logiflow.taller.service;

import com.logiflow.taller.acl.TallerAnticorruptionMapper;
import com.logiflow.taller.api.dto.ConsultaVehiculoTallerResponse;
import com.logiflow.taller.api.dto.RegistrarOrdenMantenimientoRequest;
import com.logiflow.taller.api.dto.RegistrarOrdenMantenimientoResponse;
import com.logiflow.taller.domain.OrdenMantenimiento;
import com.logiflow.taller.integration.flota.FlotaRestClient;
import com.logiflow.taller.integration.flota.VehiculoFlotaDto;
import com.logiflow.taller.repo.OrdenMantenimientoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TallerAclService {

    private final FlotaRestClient flotaRestClient;
    private final TallerAnticorruptionMapper mapper;
    private final OrdenMantenimientoRepository ordenRepository;

    public TallerAclService(
            FlotaRestClient flotaRestClient, TallerAnticorruptionMapper mapper, OrdenMantenimientoRepository ordenRepository) {
        this.flotaRestClient = flotaRestClient;
        this.mapper = mapper;
        this.ordenRepository = ordenRepository;
    }

    /**
     * Operación de negocio equivalente a {@code consultarVehiculo(matricula)} del enunciado.
     */
    @Transactional(readOnly = true)
    public ConsultaVehiculoTallerResponse consultarVehiculo(String matricula) {
        VehiculoFlotaDto flota = flotaRestClient.consultarPorMatricula(matricula);
        if (flota == null) {
            return ConsultaVehiculoTallerResponse.notFound();
        }
        return ConsultaVehiculoTallerResponse.ok(mapper.toExterno(flota));
    }

    /**
     * Operación equivalente a {@code registrarOrdenMantenimiento(matricula, descripcion)}.
     * Persiste la orden y marca el vehículo en mantenimiento en el contexto Flota.
     */
    @Transactional
    public RegistrarOrdenMantenimientoResponse registrarOrdenMantenimiento(RegistrarOrdenMantenimientoRequest req) {
        OrdenMantenimiento o = new OrdenMantenimiento();
        o.setMatricula(req.matricula().trim().toUpperCase());
        o.setDescripcion(req.descripcion());
        ordenRepository.save(o);

        VehiculoFlotaDto v = flotaRestClient.consultarPorMatricula(o.getMatricula());
        if (v != null) {
            v.setEstado("MANTENIMIENTO");
            flotaRestClient.actualizarVehiculo(v.getId(), v);
        }

        return new RegistrarOrdenMantenimientoResponse(
                o.getId(), o.getMatricula(), o.getFechaRegistro(), "Orden registrada y vehículo pasado a MANTENIMIENTO en Flota");
    }
}
