package com.odonto.api.paciente.dto;

import jakarta.validation.constraints.NotNull;

public record DadosDenteRequest(
        @NotNull(message = "Número do dente é obrigatório") Integer numeroDente,
        String cor,
        String escurecimento,
        String forma,
        String observacoes
) {}
