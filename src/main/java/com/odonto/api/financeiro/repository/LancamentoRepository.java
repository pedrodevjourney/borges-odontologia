package com.odonto.api.financeiro.repository;

import com.odonto.api.financeiro.entity.Lancamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
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

    @Query(value = """
            SELECT COALESCE(SUM(l.deve), 0),
                   COALESCE(SUM(l.haver), 0)
            FROM lancamentos l
            WHERE l.data >= :dataInicio
              AND l.data <= :dataFim
            """, nativeQuery = true)
    List<Object[]> findTotaisByPeriodo(
            @Param("dataInicio") LocalDate dataInicio,
            @Param("dataFim") LocalDate dataFim
    );

    // --- Dashboard queries ---

    @Query(value = """
            SELECT l.id, l.paciente_id, p.nome, l.descricao, l.haver, l.forma_pagamento
            FROM lancamentos l
            JOIN pacientes p ON l.paciente_id = p.id
            WHERE l.tipo = 'RECEITA'
              AND l.data = :data
              AND l.haver > 0
            ORDER BY p.nome, l.id
            """, nativeQuery = true)
    List<Object[]> findClientesReceitasHoje(@Param("data") LocalDate data);

    @Query(value = """
            SELECT COALESCE(SUM(l.haver), 0)
            FROM lancamentos l
            WHERE l.tipo = 'RECEITA'
              AND l.data = :data
            """, nativeQuery = true)
    BigDecimal findTotalReceitaHoje(@Param("data") LocalDate data);

    @Query(value = """
            SELECT COALESCE(l.forma_pagamento, 'NAO_INFORMADO') AS forma,
                   COALESCE(SUM(l.haver), 0) AS total
            FROM lancamentos l
            WHERE l.tipo = 'RECEITA'
              AND l.data BETWEEN :inicio AND :fim
            GROUP BY COALESCE(l.forma_pagamento, 'NAO_INFORMADO')
            ORDER BY total DESC
            """, nativeQuery = true)
    List<Object[]> findReceitaPorFormaPagamento(
            @Param("inicio") LocalDate inicio,
            @Param("fim") LocalDate fim
    );

    List<Lancamento> findByPacienteIdOrderByDataDesc(Long pacienteId);
}
