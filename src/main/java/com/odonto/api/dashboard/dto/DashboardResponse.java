package com.odonto.api.dashboard.dto;

import com.odonto.api.consulta.dto.ConsultaResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record DashboardResponse(
        PacientesResumo pacientes,
        ConsultasResumo consultas,
        FinanceiroResumo financeiro,
        List<ConsultaResponse> proximasConsultas,
        List<ProcedimentoContagem> procedimentosMaisRealizados
) {

    public record PacientesResumo(
            long total,
            long emTratamento
    ) {}

    public record ConsultasResumo(
            long hoje,
            long semana,
            long realizadasMes,
            long canceladasMes,
            long naoCompareceuMes,
            double taxaComparecimentoMes
    ) {}

    public record FinanceiroResumo(
            BigDecimal receitaMes,
            BigDecimal despesaMes,
            BigDecimal saldoMes
    ) {}

    public record ProcedimentoContagem(
            String tipo,
            long quantidade
    ) {}
}
