package com.odonto.api.dashboard.service;

import com.odonto.api.consulta.dto.ConsultaResponse;
import com.odonto.api.consulta.enums.StatusConsulta;
import com.odonto.api.consulta.repository.ConsultaRepository;
import com.odonto.api.dashboard.dto.DashboardResponse;
import com.odonto.api.dashboard.dto.DashboardResponse.*;
import com.odonto.api.financeiro.repository.LancamentoRepository;
import com.odonto.api.paciente.repository.PacienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    private final PacienteRepository pacienteRepository;
    private final ConsultaRepository consultaRepository;
    private final LancamentoRepository lancamentoRepository;

    public DashboardService(PacienteRepository pacienteRepository,
                            ConsultaRepository consultaRepository,
                            LancamentoRepository lancamentoRepository) {
        this.pacienteRepository = pacienteRepository;
        this.consultaRepository = consultaRepository;
        this.lancamentoRepository = lancamentoRepository;
    }

    public DashboardResponse obterDashboard() {
        LocalDate hoje = LocalDate.now();

        return new DashboardResponse(
                obterResumoPacientes(),
                obterResumoConsultas(hoje),
                obterResumoFinanceiro(hoje),
                obterProximasConsultas(),
                obterProcedimentosMaisRealizados()
        );
    }

    private PacientesResumo obterResumoPacientes() {
        return new PacientesResumo(
                pacienteRepository.count(),
                pacienteRepository.countByInicioTratamentoNotNullAndTerminoTratamentoIsNull()
        );
    }

    private ConsultasResumo obterResumoConsultas(LocalDate hoje) {
        LocalDateTime inicioDia = hoje.atStartOfDay();
        LocalDateTime fimDia = hoje.atTime(LocalTime.MAX);

        LocalDateTime inicioSemana = hoje.with(java.time.DayOfWeek.MONDAY).atStartOfDay();
        LocalDateTime fimSemana = hoje.with(java.time.DayOfWeek.SUNDAY).atTime(LocalTime.MAX);

        LocalDateTime inicioMes = hoje.with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay();
        LocalDateTime fimMes = hoje.with(TemporalAdjusters.lastDayOfMonth()).atTime(LocalTime.MAX);

        long consultasHoje = consultaRepository.countByDataHoraInicioBetween(inicioDia, fimDia);
        long consultasSemana = consultaRepository.countByDataHoraInicioBetween(inicioSemana, fimSemana);
        long realizadasMes = consultaRepository.countByDataHoraInicioBetweenAndStatus(inicioMes, fimMes, StatusConsulta.REALIZADA);
        long canceladasMes = consultaRepository.countByDataHoraInicioBetweenAndStatus(inicioMes, fimMes, StatusConsulta.CANCELADA);
        long naoCompareceuMes = consultaRepository.countByDataHoraInicioBetweenAndStatus(inicioMes, fimMes, StatusConsulta.NAO_COMPARECEU);

        long totalFinalizadasMes = realizadasMes + canceladasMes + naoCompareceuMes;
        double taxaComparecimento = totalFinalizadasMes > 0
                ? (double) realizadasMes / totalFinalizadasMes * 100
                : 0.0;

        return new ConsultasResumo(
                consultasHoje,
                consultasSemana,
                realizadasMes,
                canceladasMes,
                naoCompareceuMes,
                Math.round(taxaComparecimento * 100.0) / 100.0
        );
    }

    private FinanceiroResumo obterResumoFinanceiro(LocalDate hoje) {
        LocalDate inicioMes = hoje.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate fimMes = hoje.with(TemporalAdjusters.lastDayOfMonth());

        Object[] totais = lancamentoRepository.findTotaisByPeriodo(inicioMes, fimMes).get(0);
        BigDecimal despesa = (BigDecimal) totais[0];
        BigDecimal receita = (BigDecimal) totais[1];

        return new FinanceiroResumo(
                receita,
                despesa,
                receita.subtract(despesa)
        );
    }

    private List<ConsultaResponse> obterProximasConsultas() {
        return consultaRepository.findProximasConsultas(LocalDateTime.now(), 5)
                .stream()
                .map(ConsultaResponse::from)
                .toList();
    }

    private List<ProcedimentoContagem> obterProcedimentosMaisRealizados() {
        return consultaRepository.countByTipoRealizada()
                .stream()
                .map(row -> new ProcedimentoContagem(
                        row[0].toString(),
                        (long) row[1]
                ))
                .toList();
    }
}
