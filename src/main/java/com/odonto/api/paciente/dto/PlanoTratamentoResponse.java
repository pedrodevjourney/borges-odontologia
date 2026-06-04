package com.odonto.api.paciente.dto;

import com.odonto.api.paciente.entity.PlanoTratamento;
import com.odonto.api.paciente.enums.StatusTratamento;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record PlanoTratamentoResponse(
        Long id,
        Long pacienteId,
        String procedimento,
        Integer numeroDente,
        StatusTratamento status,
        String observacoes,
        BigDecimal valor,
        LocalDate dataPrevista,
        LocalDate dataConclusao,
        Instant createdAt,
        Instant updatedAt
) {
    public static PlanoTratamentoResponse from(PlanoTratamento p) {
        return new PlanoTratamentoResponse(
                p.getId(), p.getPaciente().getId(), p.getProcedimento(),
                p.getNumeroDente(), p.getStatus(), p.getObservacoes(),
                p.getValor(), p.getDataPrevista(), p.getDataConclusao(),
                p.getCreatedAt(), p.getUpdatedAt()
        );
    }
}
