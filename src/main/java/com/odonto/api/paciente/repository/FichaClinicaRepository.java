package com.odonto.api.paciente.repository;

import com.odonto.api.paciente.entity.FichaClinica;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FichaClinicaRepository extends JpaRepository<FichaClinica, Long> {

    List<FichaClinica> findByPacienteIdOrderByDataDesc(Long pacienteId);
}
