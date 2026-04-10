package com.odonto.api.financeiro.dto;

import java.math.BigDecimal;

public record ResumoFinanceiroResponse(
        Long pacienteId,
        String pacienteNome,
        BigDecimal totalDeve,
        BigDecimal totalHaver,
        BigDecimal saldoGeral
) {}
