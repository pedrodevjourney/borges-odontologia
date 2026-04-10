package com.odonto.api.financeiro.dto;

import com.odonto.api.financeiro.entity.Lancamento;
import com.odonto.api.financeiro.enums.TipoLancamento;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record LancamentoResponse(
        Long id,
        Long pacienteId,
        String pacienteNome,
        TipoLancamento tipo,
        String descricao,
        LocalDate data,
        BigDecimal deve,
        BigDecimal haver,
        BigDecimal saldo,
        Instant createdAt,
        Instant updatedAt
) {
    public static LancamentoResponse from(Lancamento l) {
        return new LancamentoResponse(
                l.getId(),
                l.getPaciente().getId(),
                l.getPaciente().getNome(),
                l.getTipo(),
                l.getDescricao(),
                l.getData(),
                l.getDeve(),
                l.getHaver(),
                l.getSaldo(),
                l.getCreatedAt(),
                l.getUpdatedAt()
        );
    }
}
