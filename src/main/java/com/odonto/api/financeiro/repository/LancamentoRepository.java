package com.odonto.api.financeiro.repository;

import com.odonto.api.financeiro.entity.Lancamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {

    @Query(value = """
            SELECT l.*
            FROM lancamentos l
            WHERE l.paciente_id = :pacienteId
              AND (CAST(:dataInicio AS DATE) IS NULL OR l.data >= :dataInicio)
              AND (CAST(:dataFim AS DATE) IS NULL OR l.data <= :dataFim)
              AND (CAST(:tipo AS VARCHAR) IS NULL OR l.tipo = :tipo)
            ORDER BY l.data DESC
            """, nativeQuery = true)
    List<Lancamento> findByPacienteAndFiltros(
            @Param("pacienteId") Long pacienteId,
            @Param("dataInicio") LocalDate dataInicio,
            @Param("dataFim") LocalDate dataFim,
            @Param("tipo") String tipo
    );

    @Query(value = """
            SELECT COALESCE(SUM(l.deve), 0),
                   COALESCE(SUM(l.haver), 0)
            FROM lancamentos l
            WHERE l.paciente_id = :pacienteId
              AND (CAST(:dataInicio AS DATE) IS NULL OR l.data >= :dataInicio)
              AND (CAST(:dataFim AS DATE) IS NULL OR l.data <= :dataFim)
            """, nativeQuery = true)
    List<Object[]> findTotaisByPaciente(
            @Param("pacienteId") Long pacienteId,
            @Param("dataInicio") LocalDate dataInicio,
            @Param("dataFim") LocalDate dataFim
    );
}
