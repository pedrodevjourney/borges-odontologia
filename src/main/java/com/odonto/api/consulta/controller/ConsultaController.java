package com.odonto.api.consulta.controller;

import com.odonto.api.consulta.dto.CancelarRequest;
import com.odonto.api.consulta.dto.ConsultaRequest;
import com.odonto.api.consulta.dto.ConsultaResponse;
import com.odonto.api.consulta.dto.ConsultaUpdateRequest;
import com.odonto.api.consulta.enums.StatusConsulta;
import com.odonto.api.consulta.service.ConsultaService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/consultas")
public class ConsultaController {

    private final ConsultaService consultaService;

    public ConsultaController(ConsultaService consultaService) {
        this.consultaService = consultaService;
    }

    @GetMapping
    public ResponseEntity<List<ConsultaResponse>> listar(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @RequestParam(required = false) StatusConsulta status) {
        return ResponseEntity.ok(consultaService.listar(dataInicio, dataFim, status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConsultaResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(consultaService.buscar(id));
    }

    @PostMapping
    public ResponseEntity<ConsultaResponse> criar(@Valid @RequestBody ConsultaRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(consultaService.criar(req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConsultaResponse> atualizar(
            @PathVariable Long id,
            @RequestBody ConsultaUpdateRequest req) {
        return ResponseEntity.ok(consultaService.atualizar(id, req));
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<ConsultaResponse> cancelar(
            @PathVariable Long id,
            @RequestBody(required = false) CancelarRequest req) {
        return ResponseEntity.ok(consultaService.cancelar(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        consultaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
