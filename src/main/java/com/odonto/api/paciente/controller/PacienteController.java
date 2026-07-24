package com.odonto.api.paciente.controller;

import com.odonto.api.paciente.dto.*;
import com.odonto.api.paciente.service.PacienteService;
import com.odonto.api.storage.StoredFile;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @PatchMapping("/{id}")
    public ResponseEntity<PacienteResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody PacienteUpdateRequest req) {
        return ResponseEntity.ok(pacienteService.atualizar(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        pacienteService.excluir(id);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/{pacienteId}/dados-dentes")
    public ResponseEntity<DadosDenteResponse> salvarDadosDente(
            @PathVariable Long pacienteId,
            @Valid @RequestBody DadosDenteRequest req) {
        return ResponseEntity.ok(pacienteService.salvarDadosDente(pacienteId, req));
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

    @PatchMapping("/{pacienteId}/anotacoes/{anotacaoId}")
    public ResponseEntity<AnotacaoResponse> atualizarAnotacao(
            @PathVariable Long pacienteId,
            @PathVariable Long anotacaoId,
            @Valid @RequestBody AnotacaoRequest req) {
        return ResponseEntity.ok(pacienteService.atualizarAnotacao(pacienteId, anotacaoId, req));
    }

    @DeleteMapping("/{pacienteId}/anotacoes/{anotacaoId}")
    public ResponseEntity<Void> excluirAnotacao(
            @PathVariable Long pacienteId,
            @PathVariable Long anotacaoId) {
        pacienteService.excluirAnotacao(pacienteId, anotacaoId);
        return ResponseEntity.noContent().build();
    }


    @PostMapping(value = "/{pacienteId}/radiografias", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RadiografiaResponse> criarRadiografia(
            @PathVariable Long pacienteId,
            @RequestParam("arquivo") MultipartFile arquivo,
            @RequestParam(value = "descricao", required = false) String descricao,
            @RequestParam(value = "tipo", required = false) String tipo,
            @RequestParam(value = "dataRealizacao", required = false) String dataRealizacao) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(pacienteService.criarRadiografia(pacienteId, arquivo, descricao, tipo, dataRealizacao));
    }

    @GetMapping("/{pacienteId}/radiografias")
    public ResponseEntity<List<RadiografiaResponse>> listarRadiografias(@PathVariable Long pacienteId) {
        return ResponseEntity.ok(pacienteService.listarRadiografias(pacienteId));
    }

    @GetMapping("/{pacienteId}/radiografias/{radiografiaId}/arquivo")
    public ResponseEntity<Resource> downloadRadiografia(
            @PathVariable Long pacienteId,
            @PathVariable Long radiografiaId) {
        StoredFile arquivo = pacienteService.lerArquivoRadiografia(pacienteId, radiografiaId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                .contentType(MediaType.parseMediaType(arquivo.contentType()))
                .body(arquivo.resource());
    }

    @DeleteMapping("/{pacienteId}/radiografias/{radiografiaId}")
    public ResponseEntity<Void> excluirRadiografia(
            @PathVariable Long pacienteId,
            @PathVariable Long radiografiaId) {
        pacienteService.excluirRadiografia(pacienteId, radiografiaId);
        return ResponseEntity.noContent().build();
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

    @PatchMapping("/{pacienteId}/fichas-clinicas/{fichaId}")
    public ResponseEntity<FichaClinicaResponse> atualizarFichaClinica(
            @PathVariable Long pacienteId,
            @PathVariable Long fichaId,
            @Valid @RequestBody FichaClinicaRequest req) {
        return ResponseEntity.ok(pacienteService.atualizarFichaClinica(pacienteId, fichaId, req));
    }

    @DeleteMapping("/{pacienteId}/fichas-clinicas/{fichaId}")
    public ResponseEntity<Void> excluirFichaClinica(
            @PathVariable Long pacienteId,
            @PathVariable Long fichaId) {
        pacienteService.excluirFichaClinica(pacienteId, fichaId);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/{pacienteId}/plano-tratamento")
    public ResponseEntity<PlanoTratamentoResponse> criarItemPlano(
            @PathVariable Long pacienteId,
            @Valid @RequestBody PlanoTratamentoRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(pacienteService.criarItemPlano(pacienteId, req));
    }

    @GetMapping("/{pacienteId}/plano-tratamento")
    public ResponseEntity<List<PlanoTratamentoResponse>> listarPlanoTratamento(@PathVariable Long pacienteId) {
        return ResponseEntity.ok(pacienteService.listarPlanoTratamento(pacienteId));
    }

    @PatchMapping("/{pacienteId}/plano-tratamento/{itemId}")
    public ResponseEntity<PlanoTratamentoResponse> atualizarItemPlano(
            @PathVariable Long pacienteId,
            @PathVariable Long itemId,
            @Valid @RequestBody PlanoTratamentoRequest req) {
        return ResponseEntity.ok(pacienteService.atualizarItemPlano(pacienteId, itemId, req));
    }

    @DeleteMapping("/{pacienteId}/plano-tratamento/{itemId}")
    public ResponseEntity<Void> excluirItemPlano(
            @PathVariable Long pacienteId,
            @PathVariable Long itemId) {
        pacienteService.excluirItemPlano(pacienteId, itemId);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/{pacienteId}/historico")
    public ResponseEntity<List<HistoricoItemResponse>> listarHistorico(@PathVariable Long pacienteId) {
        return ResponseEntity.ok(pacienteService.listarHistorico(pacienteId));
    }
}
