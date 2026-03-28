package com.odonto.api.consulta.dto;

import com.odonto.api.consulta.enums.TipoConsulta;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ConsultaRequest(
        @NotNull(message = "Paciente é obrigatório") Long pacienteId,
        @NotNull(message = "Data/hora de início é obrigatória") LocalDateTime dataHoraInicio,
        @NotNull(message = "Data/hora de fim é obrigatória") LocalDateTime dataHoraFim,
        @NotNull(message = "Tipo é obrigatório") TipoConsulta tipo,
        String observacoes
) {}
