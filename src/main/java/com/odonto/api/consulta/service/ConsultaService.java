package com.odonto.api.consulta.service;

import com.odonto.api.consulta.dto.*;
import com.odonto.api.consulta.entity.Consulta;
import com.odonto.api.consulta.enums.StatusConsulta;
import com.odonto.api.consulta.repository.ConsultaRepository;
import com.odonto.api.paciente.entity.Paciente;
import com.odonto.api.paciente.exception.ResourceNotFoundException;
import com.odonto.api.paciente.repository.PacienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ConsultaService {

    private final ConsultaRepository consultaRepository;
    private final PacienteRepository pacienteRepository;

    public ConsultaService(ConsultaRepository consultaRepository,
                           PacienteRepository pacienteRepository) {
        this.consultaRepository = consultaRepository;
        this.pacienteRepository = pacienteRepository;
    }

    public List<ConsultaResponse> listar(LocalDate dataInicio, LocalDate dataFim, StatusConsulta status) {
        return consultaRepository
                .findByFiltros(dataInicio.atStartOfDay(), dataFim.atTime(LocalTime.MAX), status)
                .stream()
                .map(ConsultaResponse::from)
                .toList();
    }

    public ConsultaResponse buscar(Long id) {
        return ConsultaResponse.from(findConsulta(id));
    }

    @Transactional
    public ConsultaResponse criar(ConsultaRequest req) {
        Paciente paciente = pacienteRepository.findById(req.pacienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Paciente não encontrado com id: " + req.pacienteId()));

        Consulta c = new Consulta();
        c.setPaciente(paciente);
        c.setDataHoraInicio(req.dataHoraInicio());
        c.setDataHoraFim(req.dataHoraFim());
        c.setTipo(req.tipo());
        c.setObservacoes(req.observacoes());

        return ConsultaResponse.from(consultaRepository.save(c));
    }

    @Transactional
    public ConsultaResponse atualizar(Long id, ConsultaUpdateRequest req) {
        Consulta c = findConsulta(id);

        if (req.dataHoraInicio() != null)  c.setDataHoraInicio(req.dataHoraInicio());
        if (req.dataHoraFim() != null)     c.setDataHoraFim(req.dataHoraFim());
        if (req.tipo() != null)            c.setTipo(req.tipo());
        if (req.status() != null)          c.setStatus(req.status());
        if (req.observacoes() != null)     c.setObservacoes(req.observacoes());
        if (req.googleEventId() != null)   c.setGoogleEventId(req.googleEventId());

        return ConsultaResponse.from(consultaRepository.save(c));
    }

    @Transactional
    public ConsultaResponse cancelar(Long id, CancelarRequest req) {
        Consulta c = findConsulta(id);

        if (c.getStatus() == StatusConsulta.CANCELADA) {
            throw new IllegalStateException("Consulta já está cancelada.");
        }
        if (c.getStatus() == StatusConsulta.REALIZADA) {
            throw new IllegalStateException("Não é possível cancelar uma consulta já realizada.");
        }

        c.setStatus(StatusConsulta.CANCELADA);
        c.setMotivoCancelamento(req != null ? req.motivo() : null);

        return ConsultaResponse.from(consultaRepository.save(c));
    }

    private Consulta findConsulta(Long id) {
        return consultaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Consulta não encontrada com id: " + id));
    }
}
