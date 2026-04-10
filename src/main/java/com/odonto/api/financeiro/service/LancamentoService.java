package com.odonto.api.financeiro.service;

import com.odonto.api.financeiro.dto.*;
import com.odonto.api.financeiro.entity.Lancamento;
import com.odonto.api.financeiro.enums.TipoLancamento;
import com.odonto.api.financeiro.repository.LancamentoRepository;
import com.odonto.api.paciente.entity.Paciente;
import com.odonto.api.paciente.repository.PacienteRepository;
import com.odonto.api.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class LancamentoService {

    private final LancamentoRepository lancamentoRepository;
    private final PacienteRepository pacienteRepository;

    public LancamentoService(LancamentoRepository lancamentoRepository,
                             PacienteRepository pacienteRepository) {
        this.lancamentoRepository = lancamentoRepository;
        this.pacienteRepository = pacienteRepository;
    }

    @Transactional
    public LancamentoResponse criar(LancamentoRequest req) {
        Paciente paciente = findPaciente(req.pacienteId());

        Lancamento l = new Lancamento();
        l.setPaciente(paciente);
        l.setTipo(req.tipo());
        l.setDescricao(req.descricao());
        l.setData(req.data());
        l.setDeve(req.deve());
        l.setHaver(req.haver());

        return LancamentoResponse.from(lancamentoRepository.save(l));
    }

    @Transactional
    public LancamentoResponse atualizar(Long id, LancamentoUpdateRequest req) {
        Lancamento l = findLancamento(id);

        if (req.tipo() != null)      l.setTipo(req.tipo());
        if (req.descricao() != null)  l.setDescricao(req.descricao());
        if (req.data() != null)       l.setData(req.data());
        if (req.deve() != null)       l.setDeve(req.deve());
        if (req.haver() != null)      l.setHaver(req.haver());

        return LancamentoResponse.from(lancamentoRepository.save(l));
    }

    public LancamentoResponse buscar(Long id) {
        return LancamentoResponse.from(findLancamento(id));
    }

    public List<LancamentoResponse> listarPorPaciente(Long pacienteId,
                                                       LocalDate dataInicio,
                                                       LocalDate dataFim,
                                                       TipoLancamento tipo) {
        return lancamentoRepository
                .findByPacienteAndFiltros(pacienteId, dataInicio, dataFim, tipo != null ? tipo.name() : null)
                .stream()
                .map(LancamentoResponse::from)
                .toList();
    }

    public ResumoFinanceiroResponse resumoPorPaciente(Long pacienteId,
                                                      LocalDate dataInicio,
                                                      LocalDate dataFim) {
        Paciente paciente = findPaciente(pacienteId);
        List<Object[]> resultado = lancamentoRepository.findTotaisByPaciente(pacienteId, dataInicio, dataFim);
        Object[] totais = resultado.getFirst();
        BigDecimal totalDeve = (BigDecimal) totais[0];
        BigDecimal totalHaver = (BigDecimal) totais[1];

        return new ResumoFinanceiroResponse(
                paciente.getId(),
                paciente.getNome(),
                totalDeve,
                totalHaver,
                totalHaver.subtract(totalDeve)
        );
    }

    @Transactional
    public void deletar(Long id) {
        Lancamento lancamento = findLancamento(id);
        lancamentoRepository.delete(lancamento);
    }

    private Lancamento findLancamento(Long id) {
        return lancamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lançamento não encontrado com id: " + id));
    }

    private Paciente findPaciente(Long id) {
        return pacienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente não encontrado com id: " + id));
    }
}
