package com.logiflow.clientes.service;

import com.logiflow.clientes.api.ClienteDto;
import com.logiflow.clientes.api.CreateClienteRequest;
import com.logiflow.clientes.api.UpdateClienteRequest;
import com.logiflow.clientes.domain.Cliente;
import com.logiflow.clientes.domain.ClienteRepository;
import com.logiflow.clientes.domain.TipoCliente;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ClienteService {

    private final ClienteRepository repo;

    public ClienteService(ClienteRepository repo) {
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public List<ClienteDto> listar() {
        return repo.findAll().stream().map(ClienteDto::from).toList();
    }

    @Transactional(readOnly = true)
    public ClienteDto obtener(Long id) {
        return repo.findById(id)
                .map(ClienteDto::from)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado"));
    }

    @Transactional
    public ClienteDto crear(CreateClienteRequest req) {
        Cliente c = new Cliente();
        c.setCodigo(generarCodigoUnico());
        c.setRazonSocial(req.razonSocial());
        c.setEmail(req.email());
        c.setTelefono(req.telefono());
        c.setTipo(req.tipo() != null ? req.tipo() : TipoCliente.PARTICULAR);
        c.setActivo(req.activo() == null || req.activo());
        return ClienteDto.from(repo.save(c));
    }

    @Transactional
    public ClienteDto actualizar(Long id, UpdateClienteRequest req) {
        Cliente c = repo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado"));
        c.setRazonSocial(req.razonSocial());
        c.setEmail(req.email());
        c.setTelefono(req.telefono());
        c.setTipo(req.tipo() != null ? req.tipo() : TipoCliente.PARTICULAR);
        c.setActivo(req.activo());
        c.setUpdatedAt(Instant.now());
        return ClienteDto.from(repo.save(c));
    }

    @Transactional
    public void eliminar(Long id) {
        if (!repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado");
        }
        repo.deleteById(id);
    }

    private String generarCodigoUnico() {
        String codigo;
        do {
            codigo = "CLI-" + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        } while (repo.existsByCodigo(codigo));
        return codigo;
    }
}
