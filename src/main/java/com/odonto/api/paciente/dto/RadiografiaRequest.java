package com.odonto.api.paciente.dto;

import com.odonto.api.paciente.enums.TipoRadiografia;

import java.time.LocalDate;

public record RadiografiaRequest(
        LocalDate dataRealizacao,
        String descricao,
        String caminhoArquivo,
        TipoRadiografia tipoRadiografia
) {}
