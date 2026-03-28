package com.odonto.api.paciente.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record FichaClinicaRequest(
        @NotNull(message = "Data é obrigatória") LocalDate data,
        Integer numeroDente,
        String observacoesClinicas,
        String historico,
        BigDecimal deve,
        BigDecimal haver
) {}
