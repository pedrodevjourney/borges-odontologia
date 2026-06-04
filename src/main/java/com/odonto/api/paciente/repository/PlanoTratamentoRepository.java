package com.odonto.api.paciente.repository;

import com.odonto.api.paciente.entity.PlanoTratamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlanoTratamentoRepository extends JpaRepository<PlanoTratamento, Long> {

    List<PlanoTratamento> findByPacienteIdOrderByCreatedAtAsc(Long pacienteId);
}
