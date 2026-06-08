package com.odonto.api.financeiro.dto;

import java.math.BigDecimal;

public record ResumoFinanceiroResponse(
        Long pacienteId,
        String pacienteNome,
        BigDecimal totalValorTotal,
        BigDecimal totalValorPago,
        BigDecimal valorRestanteGeral
) {}
