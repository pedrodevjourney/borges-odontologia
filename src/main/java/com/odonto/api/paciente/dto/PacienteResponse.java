package com.odonto.api.paciente.dto;

import com.odonto.api.paciente.entity.Paciente;
import com.odonto.api.paciente.enums.EstadoCivil;

import java.time.Instant;
import java.time.LocalDate;

public record PacienteResponse(
        Long id,
        Integer numero,
        String nome,
        String residencia,
        String enderecoCompleto,
        String profissao,
        LocalDate dataNascimento,
        String nacionalidade,
        String indicadoPor,
        LocalDate inicioTratamento,
        LocalDate terminoTratamento,
        LocalDate interrupcaoTratamento,
        String telefone,
        String telefoneSecundario,
        EstadoCivil estadoCivil,
        Boolean dlne,
        Instant createdAt,
        Instant updatedAt
) {
    public static PacienteResponse from(Paciente p) {
        return new PacienteResponse(
                p.getId(), p.getNumero(), p.getNome(), p.getResidencia(),
                p.getEnderecoCompleto(), p.getProfissao(), p.getDataNascimento(),
                p.getNacionalidade(), p.getIndicadoPor(), p.getInicioTratamento(),
                p.getTerminoTratamento(), p.getInterrupcaoTratamento(),
                p.getTelefone(), p.getTelefoneSecundario(), p.getEstadoCivil(),
                p.getDlne(), p.getCreatedAt(), p.getUpdatedAt()
        );
    }
}
