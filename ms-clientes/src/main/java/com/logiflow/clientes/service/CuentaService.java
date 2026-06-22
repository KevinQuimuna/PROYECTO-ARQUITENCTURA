package com.logiflow.clientes.service;

import com.logiflow.clientes.api.CreateCuentaRequest;
import com.logiflow.clientes.api.CuentaDto;
import com.logiflow.clientes.api.UpdateCuentaRequest;
import com.logiflow.clientes.domain.Cliente;
import com.logiflow.clientes.domain.ClienteRepository;
import com.logiflow.clientes.domain.CuentaCorporativa;
import com.logiflow.clientes.domain.CuentaCorporativaRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CuentaService {

    private final CuentaCorporativaRepository cuentas;
    private final ClienteRepository clientes;

    public CuentaService(CuentaCorporativaRepository cuentas, ClienteRepository clientes) {
        this.cuentas = cuentas;
        this.clientes = clientes;
    }

    @Transactional(readOnly = true)
    public List<CuentaDto> listarPorCliente(Long clienteId) {
        return cuentas.findByClienteId(clienteId).stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<CuentaDto> listarTodas() {
        return cuentas.findAll().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public CuentaDto obtener(Long id) {
        return cuentas.findById(id).map(this::toDto).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cuenta no encontrada"));
    }

    @Transactional
    public CuentaDto crear(CreateCuentaRequest req) {
        Cliente c = clientes
                .findById(req.clienteId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cliente no existe"));
        CuentaCorporativa cc = new CuentaCorporativa();
        cc.setCliente(c);
        cc.setNombreCuenta(req.nombreCuenta());
        cc.setContratoNumero(req.contratoNumero());
        cc.setSaldo(req.saldo() != null ? req.saldo() : BigDecimal.ZERO);
        cc.setLimiteCredito(req.limiteCredito() != null ? req.limiteCredito() : BigDecimal.ZERO);
        return toDto(cuentas.save(cc));
    }

    @Transactional
    public CuentaDto actualizar(Long id, UpdateCuentaRequest req) {
        CuentaCorporativa cc = cuentas.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cuenta no encontrada"));
        cc.setNombreCuenta(req.nombreCuenta());
        cc.setContratoNumero(req.contratoNumero());
        if (req.saldo() != null) {
            cc.setSaldo(req.saldo());
        }
        if (req.limiteCredito() != null) {
            cc.setLimiteCredito(req.limiteCredito());
        }
        return toDto(cuentas.save(cc));
    }

    @Transactional
    public void eliminar(Long id) {
        if (!cuentas.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cuenta no encontrada");
        }
        cuentas.deleteById(id);
    }

    private CuentaDto toDto(CuentaCorporativa cc) {
        return new CuentaDto(
                cc.getId(),
                cc.getCliente().getId(),
                cc.getNombreCuenta(),
                cc.getContratoNumero(),
                cc.getSaldo(),
                cc.getLimiteCredito());
    }
}
