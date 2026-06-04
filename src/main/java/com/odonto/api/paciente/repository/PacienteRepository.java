package com.odonto.api.paciente.repository;

import com.odonto.api.paciente.entity.Paciente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    Page<Paciente> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

    long count();

    long countByInicioTratamentoNotNullAndTerminoTratamentoIsNull();

    @Query("""
            SELECT p FROM Paciente p
            WHERE p.inicioTratamento IS NOT NULL
              AND p.terminoTratamento IS NULL
              AND (
                NOT EXISTS (
                  SELECT c FROM Consulta c
                  WHERE c.paciente = p AND c.status = com.odonto.api.consulta.enums.StatusConsulta.REALIZADA
                )
                OR (
                  SELECT MAX(c.dataHoraInicio) FROM Consulta c
                  WHERE c.paciente = p AND c.status = com.odonto.api.consulta.enums.StatusConsulta.REALIZADA
                ) < :dataLimite
              )
            ORDER BY p.nome ASC
            """)
    List<Paciente> findPacientesSemRetorno(@Param("dataLimite") LocalDateTime dataLimite);
}
