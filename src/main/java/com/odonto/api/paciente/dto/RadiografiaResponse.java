package com.odonto.api.paciente.dto;

import com.odonto.api.paciente.entity.Radiografia;
import com.odonto.api.paciente.enums.TipoRadiografia;

import java.time.Instant;
import java.time.LocalDate;

public record RadiografiaResponse(
        Long id,
        Long pacienteId,
        LocalDate dataRealizacao,
        String descricao,
        String caminhoArquivo,
        TipoRadiografia tipoRadiografia,
        Instant createdAt
) {
    public static RadiografiaResponse from(Radiografia r) {
        return new RadiografiaResponse(
                r.getId(), r.getPaciente().getId(), r.getDataRealizacao(),
                r.getDescricao(), r.getCaminhoArquivo(), r.getTipoRadiografia(),
                r.getCreatedAt()
        );
    }
}
