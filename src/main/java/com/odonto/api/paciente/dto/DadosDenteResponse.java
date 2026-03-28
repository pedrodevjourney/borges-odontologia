package com.odonto.api.paciente.dto;

import com.odonto.api.paciente.entity.DadosDente;

public record DadosDenteResponse(
        Long id,
        Long pacienteId,
        Integer numeroDente,
        String cor,
        String escurecimento,
        String forma,
        String observacoes
) {
    public static DadosDenteResponse from(DadosDente d) {
        return new DadosDenteResponse(
                d.getId(), d.getPaciente().getId(), d.getNumeroDente(),
                d.getCor(), d.getEscurecimento(), d.getForma(), d.getObservacoes()
        );
    }
}
