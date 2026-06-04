package com.odonto.api.paciente.dto;

import com.odonto.api.paciente.enums.StatusTratamento;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PlanoTratamentoRequest(
        @NotBlank(message = "Procedimento é obrigatório") String procedimento,
        Integer numeroDente,
        StatusTratamento status,
        String observacoes,
        BigDecimal valor,
        LocalDate dataPrevista,
        LocalDate dataConclusao
) {}
