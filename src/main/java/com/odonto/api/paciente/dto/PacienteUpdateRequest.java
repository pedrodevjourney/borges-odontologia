package com.odonto.api.paciente.dto;

import com.odonto.api.paciente.enums.EstadoCivil;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

public record PacienteUpdateRequest(
        String nome,
        @Pattern(
            regexp = "^\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}$",
            message = "CPF deve estar no formato 000.000.000-00"
        ) String cpf,
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
        Boolean prospecto
) {}
