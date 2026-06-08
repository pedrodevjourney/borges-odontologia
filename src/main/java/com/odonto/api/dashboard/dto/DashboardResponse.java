package com.odonto.api.dashboard.dto;

import com.odonto.api.consulta.dto.ConsultaResponse;

import java.math.BigDecimal;
import java.util.List;

public record DashboardResponse(
        PacientesResumo pacientes,
        ConsultasResumo consultas,
        FinanceiroResumo financeiro,
        RecebimentoHoje recebimentoHoje,
        List<ConsultaResponse> proximasConsultas,
        List<ProcedimentoContagem> procedimentosMaisRealizados,
        List<AlertaRetorno> alertasRetorno
) {

    public record PacientesResumo(
            long total,
            long emTratamento
    ) {}

    public record AlertaRetorno(
            Long pacienteId,
            String pacienteNome,
            String ultimaConsulta
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
            BigDecimal saldoMes,
            List<RecebimentoPorForma> receitaPorFormaMes
    ) {}

    public record RecebimentoHoje(
            BigDecimal total,
            List<RecebimentoPorForma> porForma,
            List<ClienteHoje> clientes
    ) {}

    public record RecebimentoPorForma(
            String formaPagamento,
            BigDecimal valor
    ) {}

    public record ClienteHoje(
            Long pacienteId,
            String pacienteNome,
            Long lancamentoId,
            String descricao,
            BigDecimal valorPago,
            String formaPagamento
    ) {}

    public record ProcedimentoContagem(
            String tipo,
            long quantidade
    ) {}
}
