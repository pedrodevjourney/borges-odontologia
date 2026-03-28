package com.odonto.api.paciente.repository;

import com.odonto.api.paciente.entity.Anotacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnotacaoRepository extends JpaRepository<Anotacao, Long> {

    List<Anotacao> findByPacienteIdOrderByDataAnotacaoDesc(Long pacienteId);
}
