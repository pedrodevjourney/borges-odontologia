package com.odonto.api.dashboard.service;

import com.odonto.api.consulta.dto.ConsultaResponse;
import com.odonto.api.consulta.enums.StatusConsulta;
import com.odonto.api.consulta.repository.ConsultaRepository;
import com.odonto.api.paciente.repository.PacienteRepository;
import com.odonto.api.dashboard.dto.DashboardResponse;
import com.odonto.api.dashboard.dto.DashboardResponse.*;
import com.odonto.api.financeiro.repository.LancamentoRepository;
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
                obterRecebimentoHoje(hoje),
                obterProximasConsultas(),
                obterProcedimentosMaisRealizados(),
                obterAlertasRetorno()
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

        List<RecebimentoPorForma> receitaPorFormaMes = lancamentoRepository
                .findReceitaPorFormaPagamento(inicioMes, fimMes)
                .stream()
                .map(row -> new RecebimentoPorForma(
                        row[0] != null ? row[0].toString() : "NAO_INFORMADO",
                        (BigDecimal) row[1]
                ))
                .toList();

        return new FinanceiroResumo(
                receita,
                despesa,
                receita.subtract(despesa),
                receitaPorFormaMes
        );
    }

    private RecebimentoHoje obterRecebimentoHoje(LocalDate hoje) {
        BigDecimal total = lancamentoRepository.findTotalReceitaHoje(hoje);
        if (total == null) total = BigDecimal.ZERO;

        List<RecebimentoPorForma> porForma = lancamentoRepository
                .findReceitaPorFormaPagamento(hoje, hoje)
                .stream()
                .map(row -> new RecebimentoPorForma(
                        row[0] != null ? row[0].toString() : "NAO_INFORMADO",
                        (BigDecimal) row[1]
                ))
                .toList();

        List<ClienteHoje> clientes = lancamentoRepository
                .findClientesReceitasHoje(hoje)
                .stream()
                .map(row -> new ClienteHoje(
                        ((Number) row[1]).longValue(),
                        row[2] != null ? row[2].toString() : "",
                        ((Number) row[0]).longValue(),
                        row[3] != null ? row[3].toString() : "",
                        (BigDecimal) row[4],
                        row[5] != null ? row[5].toString() : null
                ))
                .toList();

        return new RecebimentoHoje(total, porForma, clientes);
    }

    private List<ConsultaResponse> obterProximasConsultas() {
        var statusPermitidos = List.of(StatusConsulta.AGENDADA, StatusConsulta.CONFIRMADA);
        return consultaRepository.findProximasConsultas(LocalDateTime.now(), statusPermitidos, 5)
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

    private List<DashboardResponse.AlertaRetorno> obterAlertasRetorno() {
        LocalDateTime tresMesesAtras = LocalDateTime.now().minusMonths(3);
        return pacienteRepository.findPacientesSemRetorno(tresMesesAtras)
                .stream()
                .map(p -> {
                    var consultas = consultaRepository.findByPacienteIdOrderByDataHoraInicioDesc(p.getId());
                    String ultimaConsulta = consultas.stream()
                            .filter(c -> c.getStatus() == StatusConsulta.REALIZADA)
                            .findFirst()
                            .map(c -> c.getDataHoraInicio().toLocalDate().toString())
                            .orElse(null);
                    return new DashboardResponse.AlertaRetorno(p.getId(), p.getNome(), ultimaConsulta);
                })
                .toList();
    }
}
