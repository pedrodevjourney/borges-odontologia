package com.odonto.api.consulta.repository;

import com.odonto.api.consulta.model.Consulta;
import com.odonto.api.consulta.enums.StatusConsulta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;


public interface ConsultaRepository extends JpaRepository<Consulta, Long> {

    @Query("""
            SELECT c FROM Consulta c
            JOIN FETCH c.paciente
            WHERE c.dataHoraInicio >= :dataInicio
              AND c.dataHoraInicio <= :dataFim
              AND (:status IS NULL OR c.status = :status)
            ORDER BY c.dataHoraInicio ASC
            """)
    List<Consulta> findByFiltros(
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim,
            @Param("status") StatusConsulta status
    );
}
