package com.odonto.api.paciente.dto;

import com.odonto.api.paciente.entity.Anotacao;

import java.time.Instant;
import java.time.LocalDate;

public record AnotacaoResponse(
        Long id,
        Long pacienteId,
        LocalDate dataAnotacao,
        String conteudo,
        Instant createdAt
) {
    public static AnotacaoResponse from(Anotacao a) {
        return new AnotacaoResponse(
                a.getId(), a.getPaciente().getId(), a.getDataAnotacao(),
                a.getConteudo(), a.getCreatedAt()
        );
    }
}
