package com.odonto.api.paciente.dto;

import java.time.LocalDate;

public record HistoricoItemResponse(
        Long id,
        String tipo,
        LocalDate data,
        String titulo,
        String descricao,
        Long referenceId
) {}
