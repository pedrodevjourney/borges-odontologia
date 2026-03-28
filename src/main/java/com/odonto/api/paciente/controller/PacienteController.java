package com.odonto.api.paciente.controller;

import com.odonto.api.paciente.dto.*;
import com.odonto.api.paciente.service.PacienteService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pacientes")
public class PacienteController {

    private final PacienteService pacienteService;

    public PacienteController(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }


    @PostMapping
    public ResponseEntity<PacienteResponse> criar(@Valid @RequestBody PacienteRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pacienteService.criar(req));
    }

    @GetMapping
    public ResponseEntity<Page<PacienteResponse>> listar(
            @RequestParam(required = false) String nome,
            @PageableDefault(size = 20, sort = "nome") Pageable pageable) {
        return ResponseEntity.ok(pacienteService.listar(nome, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PacienteResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(pacienteService.buscar(id));
    }


    @PostMapping("/{pacienteId}/dados-dentes")
    public ResponseEntity<DadosDenteResponse> criarDadosDente(
            @PathVariable Long pacienteId,
            @Valid @RequestBody DadosDenteRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pacienteService.criarDadosDente(pacienteId, req));
    }

    @GetMapping("/{pacienteId}/dados-dentes")
    public ResponseEntity<List<DadosDenteResponse>> listarDadosDentes(@PathVariable Long pacienteId) {
        return ResponseEntity.ok(pacienteService.listarDadosDentes(pacienteId));
    }


    @PostMapping("/{pacienteId}/anotacoes")
    public ResponseEntity<AnotacaoResponse> criarAnotacao(
            @PathVariable Long pacienteId,
            @Valid @RequestBody AnotacaoRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pacienteService.criarAnotacao(pacienteId, req));
    }

    @GetMapping("/{pacienteId}/anotacoes")
    public ResponseEntity<List<AnotacaoResponse>> listarAnotacoes(@PathVariable Long pacienteId) {
        return ResponseEntity.ok(pacienteService.listarAnotacoes(pacienteId));
    }


    @PostMapping("/{pacienteId}/radiografias")
    public ResponseEntity<RadiografiaResponse> criarRadiografia(
            @PathVariable Long pacienteId,
            @Valid @RequestBody RadiografiaRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pacienteService.criarRadiografia(pacienteId, req));
    }

    @GetMapping("/{pacienteId}/radiografias")
    public ResponseEntity<List<RadiografiaResponse>> listarRadiografias(@PathVariable Long pacienteId) {
        return ResponseEntity.ok(pacienteService.listarRadiografias(pacienteId));
    }


    @PostMapping("/{pacienteId}/fichas-clinicas")
    public ResponseEntity<FichaClinicaResponse> criarFichaClinica(
            @PathVariable Long pacienteId,
            @Valid @RequestBody FichaClinicaRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pacienteService.criarFichaClinica(pacienteId, req));
    }

    @GetMapping("/{pacienteId}/fichas-clinicas")
    public ResponseEntity<List<FichaClinicaResponse>> listarFichasClinicas(@PathVariable Long pacienteId) {
        return ResponseEntity.ok(pacienteService.listarFichasClinicas(pacienteId));
    }
}
