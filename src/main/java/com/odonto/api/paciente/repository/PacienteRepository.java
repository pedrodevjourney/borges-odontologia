package com.odonto.api.paciente.repository;

import com.odonto.api.paciente.entity.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    Page<Paciente> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

    Optional<Paciente> findByNumero(Integer numero);
}
