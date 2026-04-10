package com.odonto.api.financeiro.dto;

import com.odonto.api.financeiro.enums.TipoLancamento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LancamentoRequest(
        @NotNull(message = "Paciente é obrigatório") Long pacienteId,
        @NotNull(message = "Tipo é obrigatório") TipoLancamento tipo,
        @NotBlank(message = "Descrição é obrigatória") String descricao,
        @NotNull(message = "Data é obrigatória") LocalDate data,
        BigDecimal deve,
        BigDecimal haver
) {}
