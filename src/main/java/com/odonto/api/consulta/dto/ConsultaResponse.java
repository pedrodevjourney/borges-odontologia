package com.odonto.api.consulta.dto;

import com.odonto.api.consulta.entity.Consulta;
import com.odonto.api.consulta.enums.StatusConsulta;
import com.odonto.api.consulta.enums.TipoConsulta;

import java.time.Instant;
import java.time.LocalDateTime;

public record ConsultaResponse(
        Long id,
        Long pacienteId,
        String pacienteNome,
        LocalDateTime dataHoraInicio,
        LocalDateTime dataHoraFim,
        StatusConsulta status,
        TipoConsulta tipo,
        String observacoes,
        String motivoCancelamento,
        Instant createdAt,
        Instant updatedAt
) {
    public static ConsultaResponse from(Consulta c) {
        return new ConsultaResponse(
                c.getId(),
                c.getPaciente().getId(),
                c.getPaciente().getNome(),
                c.getDataHoraInicio(),
                c.getDataHoraFim(),
                c.getStatus(),
                c.getTipo(),
                c.getObservacoes(),
                c.getMotivoCancelamento(),
                c.getCreatedAt(),
                c.getUpdatedAt()
        );
    }
}
