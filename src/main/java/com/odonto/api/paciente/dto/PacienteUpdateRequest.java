package com.odonto.api.paciente.dto;

import com.odonto.api.paciente.enums.EstadoCivil;

import java.time.LocalDate;

public record PacienteUpdateRequest(
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
        Boolean dlne
) {}
