package com.odonto.api.paciente.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record AnotacaoRequest(
        LocalDate dataAnotacao,
        @NotBlank(message = "Conteúdo é obrigatório") String conteudo
) {}
