package com.odonto.api.paciente.dto;

import com.odonto.api.paciente.enums.EstadoCivil;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record PacienteRequest(
        Integer numero,
        @NotBlank(message = "Nome é obrigatório") String nome,
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
