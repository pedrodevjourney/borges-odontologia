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

    long countByDataHoraInicioBetween(LocalDateTime inicio, LocalDateTime fim);

    long countByDataHoraInicioBetweenAndStatus(LocalDateTime inicio, LocalDateTime fim, StatusConsulta status);

    @Query("""
            SELECT c.tipo, COUNT(c) FROM Consulta c
            WHERE c.status = com.odonto.api.consulta.enums.StatusConsulta.REALIZADA
            GROUP BY c.tipo
            ORDER BY COUNT(c) DESC
            """)
    List<Object[]> countByTipoRealizada();

    @Query("""
            SELECT c FROM Consulta c
            JOIN FETCH c.paciente
            WHERE c.dataHoraInicio >= :agora
              AND c.status IN (:statusList)
            ORDER BY c.dataHoraInicio ASC
            LIMIT :limite
            """)
    List<Consulta> findProximasConsultas(
            @Param("agora") LocalDateTime agora,
            @Param("statusList") List<StatusConsulta> statusList,
            @Param("limite") int limite
    );
}
