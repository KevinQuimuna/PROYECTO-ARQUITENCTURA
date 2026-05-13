package com.logiflow.taller.acl;

import static org.assertj.core.api.Assertions.assertThat;

import com.logiflow.taller.api.dto.VehiculoTallerExterno;
import com.logiflow.taller.integration.flota.VehiculoFlotaDto;
import org.junit.jupiter.api.Test;

class TallerAnticorruptionMapperTest {

    private final TallerAnticorruptionMapper mapper = new TallerAnticorruptionMapper();

    @Test
    void mapeaDesdeFlota() {
        VehiculoFlotaDto f = new VehiculoFlotaDto();
        f.setMatricula("ABC1");
        f.setTipo("MOTO");
        f.setCapacidadKg(40d);
        f.setEstado("DISPONIBLE");
        f.setAutonomiaKm(90);

        VehiculoTallerExterno e = mapper.toExterno(f);
        assertThat(e.getTipoEquipo()).isEqualTo("EQ-MOTO");
        assertThat(e.getEstadoOperativoTaller()).isEqualTo("OPERATIVO");
        assertThat(e.getMasaMaximaKg()).isEqualTo(40d);
    }
}
