package com.odonto.api.consulta.dto;

import com.odonto.api.consulta.enums.StatusConsulta;
import com.odonto.api.consulta.enums.TipoConsulta;

import java.time.LocalDateTime;

public record ConsultaUpdateRequest(
        LocalDateTime dataHoraInicio,
        LocalDateTime dataHoraFim,
        TipoConsulta tipo,
        StatusConsulta status,
        String observacoes,
        String googleEventId
) {}
