package com.odonto.api.paciente.repository;

import com.odonto.api.paciente.entity.Radiografia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RadiografiaRepository extends JpaRepository<Radiografia, Long> {

    List<Radiografia> findByPacienteId(Long pacienteId);
}
