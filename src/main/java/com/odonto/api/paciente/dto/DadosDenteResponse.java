package com.odonto.api.paciente.dto;

import com.odonto.api.paciente.entity.DadosDente;
import com.odonto.api.paciente.enums.StatusDente;

import java.time.Instant;

public record DadosDenteResponse(
        Long id,
        Long pacienteId,
        Integer numeroDente,
        StatusDente status,
        String cor,
        String escurecimento,
        String forma,
        String observacoes,
        Instant updatedAt
) {
    public static DadosDenteResponse from(DadosDente d) {
        return new DadosDenteResponse(
                d.getId(), d.getPaciente().getId(), d.getNumeroDente(),
                d.getStatus(), d.getCor(), d.getEscurecimento(),
                d.getForma(), d.getObservacoes(), d.getUpdatedAt()
        );
    }
}
