package com.odonto.api.financeiro.controller;

import com.odonto.api.financeiro.dto.*;
import com.odonto.api.financeiro.enums.TipoLancamento;
import com.odonto.api.financeiro.service.LancamentoService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/lancamentos")
public class LancamentoController {

    private final LancamentoService lancamentoService;

    public LancamentoController(LancamentoService lancamentoService) {
        this.lancamentoService = lancamentoService;
    }

    @PostMapping
    public ResponseEntity<LancamentoResponse> criar(@Valid @RequestBody LancamentoRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(lancamentoService.criar(req));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LancamentoResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(lancamentoService.buscar(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LancamentoResponse> atualizar(@PathVariable Long id,
                                                         @RequestBody LancamentoUpdateRequest req) {
        return ResponseEntity.ok(lancamentoService.atualizar(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        lancamentoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<List<LancamentoResponse>> listarPorPaciente(
            @PathVariable Long pacienteId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @RequestParam(required = false) TipoLancamento tipo) {
        return ResponseEntity.ok(lancamentoService.listarPorPaciente(pacienteId, dataInicio, dataFim, tipo));
    }

    @GetMapping("/paciente/{pacienteId}/resumo")
    public ResponseEntity<ResumoFinanceiroResponse> resumoPorPaciente(
            @PathVariable Long pacienteId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        return ResponseEntity.ok(lancamentoService.resumoPorPaciente(pacienteId, dataInicio, dataFim));
    }
}
