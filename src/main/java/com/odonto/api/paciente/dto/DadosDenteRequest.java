package com.odonto.api.paciente.dto;

import com.odonto.api.paciente.enums.StatusDente;
import jakarta.validation.constraints.NotNull;

public record DadosDenteRequest(
        @NotNull(message = "Número do dente é obrigatório") Integer numeroDente,
        StatusDente status,
        String cor,
        String escurecimento,
        String forma,
        String observacoes
) {}
