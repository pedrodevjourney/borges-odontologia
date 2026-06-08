package com.odonto.api.financeiro.dto;

import com.odonto.api.financeiro.enums.FormaPagamento;
import com.odonto.api.financeiro.enums.TipoLancamento;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LancamentoUpdateRequest(
        TipoLancamento tipo,
        String descricao,
        LocalDate data,
        BigDecimal valorTotal,
        BigDecimal valorPago,
        FormaPagamento formaPagamento
) {}
