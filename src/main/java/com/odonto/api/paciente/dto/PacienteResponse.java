package com.odonto.api.paciente.dto;

import com.odonto.api.paciente.entity.Paciente;
import com.odonto.api.paciente.enums.EstadoCivil;

import java.time.Instant;
import java.time.LocalDate;

public record PacienteResponse(
        Long id,
        String nome,
        String cpf,
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
        Instant createdAt,
        Instant updatedAt
) {
    public static PacienteResponse from(Paciente p) {
        return new PacienteResponse(
                p.getId(), p.getNome(), p.getCpf(), p.getResidencia(),
                p.getEnderecoCompleto(), p.getProfissao(), p.getDataNascimento(),
                p.getNacionalidade(), p.getIndicadoPor(), p.getInicioTratamento(),
                p.getTerminoTratamento(), p.getInterrupcaoTratamento(),
                p.getTelefone(), p.getTelefoneSecundario(), p.getEstadoCivil(),
                p.getCreatedAt(), p.getUpdatedAt()
        );
    }
}
