package com.odonto.api.paciente.repository;

import com.odonto.api.paciente.entity.DadosDente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DadosDenteRepository extends JpaRepository<DadosDente, Long> {

    List<DadosDente> findByPacienteId(Long pacienteId);

    Optional<DadosDente> findByPacienteIdAndNumeroDente(Long pacienteId, Integer numeroDente);
}
