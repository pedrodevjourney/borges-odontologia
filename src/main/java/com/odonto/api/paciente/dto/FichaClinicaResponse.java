package com.odonto.api.paciente.dto;

import com.odonto.api.paciente.entity.FichaClinica;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record FichaClinicaResponse(
        Long id,
        Long pacienteId,
        LocalDate data,
        Integer numeroDente,
        String observacoesClinicas,
        String historico,
        BigDecimal deve,
        BigDecimal haver,
        BigDecimal saldo,
        Instant createdAt
) {
    public static FichaClinicaResponse from(FichaClinica f) {
        return new FichaClinicaResponse(
                f.getId(), f.getPaciente().getId(), f.getData(),
                f.getNumeroDente(), f.getObservacoesClinicas(), f.getHistorico(),
                f.getDeve(), f.getHaver(), f.getSaldo(), f.getCreatedAt()
        );
    }
}
