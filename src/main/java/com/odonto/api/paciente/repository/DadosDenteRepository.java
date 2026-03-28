package com.odonto.api.paciente.repository;

import com.odonto.api.paciente.entity.DadosDente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DadosDenteRepository extends JpaRepository<DadosDente, Long> {

    List<DadosDente> findByPacienteId(Long pacienteId);
}
