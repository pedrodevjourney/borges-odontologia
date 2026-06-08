package com.odonto.api.paciente.service;

import com.odonto.api.consulta.repository.ConsultaRepository;
import com.odonto.api.financeiro.repository.LancamentoRepository;
import com.odonto.api.paciente.dto.*;
import com.odonto.api.paciente.entity.*;
import com.odonto.api.exception.ResourceNotFoundException;
import com.odonto.api.paciente.enums.StatusTratamento;
import com.odonto.api.paciente.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Stream;

@Service
@Transactional(readOnly = true)
public class PacienteService {

    private final PacienteRepository pacienteRepository;
    private final DadosDenteRepository dadosDenteRepository;
    private final AnotacaoRepository anotacaoRepository;
    private final RadiografiaRepository radiografiaRepository;
    private final FichaClinicaRepository fichaClinicaRepository;
    private final PlanoTratamentoRepository planoTratamentoRepository;
    private final ConsultaRepository consultaRepository;
    private final LancamentoRepository lancamentoRepository;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    public PacienteService(PacienteRepository pacienteRepository,
                           DadosDenteRepository dadosDenteRepository,
                           AnotacaoRepository anotacaoRepository,
                           RadiografiaRepository radiografiaRepository,
                           FichaClinicaRepository fichaClinicaRepository,
                           PlanoTratamentoRepository planoTratamentoRepository,
                           ConsultaRepository consultaRepository,
                           LancamentoRepository lancamentoRepository) {
        this.pacienteRepository = pacienteRepository;
        this.dadosDenteRepository = dadosDenteRepository;
        this.anotacaoRepository = anotacaoRepository;
        this.radiografiaRepository = radiografiaRepository;
        this.fichaClinicaRepository = fichaClinicaRepository;
        this.planoTratamentoRepository = planoTratamentoRepository;
        this.consultaRepository = consultaRepository;
        this.lancamentoRepository = lancamentoRepository;
    }


    @Transactional
    public PacienteResponse criar(PacienteRequest req) {
        Paciente p = new Paciente();
        p.setNome(req.nome());
        p.setResidencia(req.residencia());
        p.setEnderecoCompleto(req.enderecoCompleto());
        p.setProfissao(req.profissao());
        p.setDataNascimento(req.dataNascimento());
        p.setNacionalidade(req.nacionalidade());
        p.setIndicadoPor(req.indicadoPor());
        p.setInicioTratamento(req.inicioTratamento());
        p.setTerminoTratamento(req.terminoTratamento());
        p.setInterrupcaoTratamento(req.interrupcaoTratamento());
        p.setTelefone(req.telefone());
        p.setTelefoneSecundario(req.telefoneSecundario());
        p.setEstadoCivil(req.estadoCivil());
        p.setCpf(req.cpf());
        return PacienteResponse.from(pacienteRepository.save(p));
    }

    @Transactional
    public PacienteResponse atualizar(Long id, PacienteUpdateRequest req) {
        Paciente p = findPaciente(id);
        if (req.nome() != null && !req.nome().isBlank()) p.setNome(req.nome());
        if (req.cpf() != null) p.setCpf(req.cpf().isBlank() ? null : req.cpf());
        if (req.residencia() != null) p.setResidencia(req.residencia());
        if (req.enderecoCompleto() != null) p.setEnderecoCompleto(req.enderecoCompleto());
        if (req.profissao() != null) p.setProfissao(req.profissao());
        if (req.dataNascimento() != null) p.setDataNascimento(req.dataNascimento());
        if (req.nacionalidade() != null) p.setNacionalidade(req.nacionalidade());
        if (req.indicadoPor() != null) p.setIndicadoPor(req.indicadoPor());
        if (req.inicioTratamento() != null) p.setInicioTratamento(req.inicioTratamento());
        if (req.terminoTratamento() != null) p.setTerminoTratamento(req.terminoTratamento());
        if (req.interrupcaoTratamento() != null) p.setInterrupcaoTratamento(req.interrupcaoTratamento());
        if (req.telefone() != null) p.setTelefone(req.telefone());
        if (req.telefoneSecundario() != null) p.setTelefoneSecundario(req.telefoneSecundario());
        if (req.estadoCivil() != null) p.setEstadoCivil(req.estadoCivil());
        return PacienteResponse.from(pacienteRepository.save(p));
    }

    public Page<PacienteResponse> listar(String nome, Pageable pageable) {
        Page<Paciente> page = (nome != null && !nome.isBlank())
                ? pacienteRepository.findByNomeContainingIgnoreCase(nome, pageable)
                : pacienteRepository.findAll(pageable);
        return page.map(PacienteResponse::from);
    }

    public PacienteResponse buscar(Long id) {
        return PacienteResponse.from(findPaciente(id));
    }


    @Transactional
    public DadosDenteResponse salvarDadosDente(Long pacienteId, DadosDenteRequest req) {
        Paciente paciente = findPaciente(pacienteId);
        DadosDente d = dadosDenteRepository
                .findByPacienteIdAndNumeroDente(pacienteId, req.numeroDente())
                .orElse(new DadosDente());
        d.setPaciente(paciente);
        d.setNumeroDente(req.numeroDente());
        d.setStatus(req.status());
        d.setCor(req.cor());
        d.setEscurecimento(req.escurecimento());
        d.setForma(req.forma());
        d.setObservacoes(req.observacoes());
        return DadosDenteResponse.from(dadosDenteRepository.save(d));
    }

    public List<DadosDenteResponse> listarDadosDentes(Long pacienteId) {
        findPaciente(pacienteId);
        return dadosDenteRepository.findByPacienteId(pacienteId).stream()
                .map(DadosDenteResponse::from).toList();
    }


    @Transactional
    public AnotacaoResponse criarAnotacao(Long pacienteId, AnotacaoRequest req) {
        Paciente paciente = findPaciente(pacienteId);
        Anotacao a = new Anotacao();
        a.setPaciente(paciente);
        a.setDataAnotacao(req.dataAnotacao());
        a.setConteudo(req.conteudo());
        return AnotacaoResponse.from(anotacaoRepository.save(a));
    }

    public List<AnotacaoResponse> listarAnotacoes(Long pacienteId) {
        findPaciente(pacienteId);
        return anotacaoRepository.findByPacienteIdOrderByDataAnotacaoDesc(pacienteId).stream()
                .map(AnotacaoResponse::from).toList();
    }

    @Transactional
    public AnotacaoResponse atualizarAnotacao(Long pacienteId, Long anotacaoId, AnotacaoRequest req) {
        findPaciente(pacienteId);
        Anotacao a = anotacaoRepository.findById(anotacaoId)
                .orElseThrow(() -> new ResourceNotFoundException("Anotação não encontrada com id: " + anotacaoId));
        a.setDataAnotacao(req.dataAnotacao());
        a.setConteudo(req.conteudo());
        return AnotacaoResponse.from(anotacaoRepository.save(a));
    }

    @Transactional
    public void excluirAnotacao(Long pacienteId, Long anotacaoId) {
        findPaciente(pacienteId);
        Anotacao a = anotacaoRepository.findById(anotacaoId)
                .orElseThrow(() -> new ResourceNotFoundException("Anotação não encontrada com id: " + anotacaoId));
        anotacaoRepository.delete(a);
    }


    @Transactional
    public RadiografiaResponse criarRadiografia(Long pacienteId, MultipartFile arquivo,
                                                 String descricao, String tipoStr, String dataStr) {
        Paciente paciente = findPaciente(pacienteId);

        String nomeOriginal = arquivo.getOriginalFilename() != null
                ? arquivo.getOriginalFilename() : "arquivo";
        String extensao = nomeOriginal.contains(".")
                ? nomeOriginal.substring(nomeOriginal.lastIndexOf(".")) : "";
        String nomeArmazenado = UUID.randomUUID() + extensao;

        Path dirPath = Paths.get(uploadDir, "radiografias", String.valueOf(pacienteId));
        try {
            Files.createDirectories(dirPath);
            Path destino = dirPath.resolve(nomeArmazenado);
            Files.copy(arquivo.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Falha ao salvar arquivo: " + e.getMessage());
        }

        Radiografia r = new Radiografia();
        r.setPaciente(paciente);
        r.setNomeOriginal(nomeOriginal);
        r.setNomeArmazenado(nomeArmazenado);
        r.setContentType(arquivo.getContentType());
        r.setCaminhoArquivo(Paths.get("radiografias", String.valueOf(pacienteId), nomeArmazenado).toString());
        r.setDescricao(descricao);
        r.setDataRealizacao(dataStr != null && !dataStr.isBlank() ? LocalDate.parse(dataStr) : null);

        if (tipoStr != null && !tipoStr.isBlank()) {
            try {
                r.setTipoRadiografia(com.odonto.api.paciente.enums.TipoRadiografia.valueOf(tipoStr));
            } catch (IllegalArgumentException ignored) {}
        }

        return RadiografiaResponse.from(radiografiaRepository.save(r));
    }

    public List<RadiografiaResponse> listarRadiografias(Long pacienteId) {
        findPaciente(pacienteId);
        return radiografiaRepository.findByPacienteId(pacienteId).stream()
                .map(RadiografiaResponse::from).toList();
    }

    public Path resolverArquivoRadiografia(Long pacienteId, Long radiografiaId) {
        Radiografia r = radiografiaRepository.findById(radiografiaId)
                .orElseThrow(() -> new ResourceNotFoundException("Radiografia não encontrada com id: " + radiografiaId));
        if (!r.getPaciente().getId().equals(pacienteId)) {
            throw new ResourceNotFoundException("Radiografia não pertence a este paciente");
        }
        return Paths.get(uploadDir).resolve(r.getCaminhoArquivo());
    }

    @Transactional
    public void excluirRadiografia(Long pacienteId, Long radiografiaId) {
        Radiografia r = radiografiaRepository.findById(radiografiaId)
                .orElseThrow(() -> new ResourceNotFoundException("Radiografia não encontrada com id: " + radiografiaId));
        if (!r.getPaciente().getId().equals(pacienteId)) {
            throw new ResourceNotFoundException("Radiografia não pertence a este paciente");
        }
        Path arquivo = Paths.get(uploadDir).resolve(r.getCaminhoArquivo());
        try { Files.deleteIfExists(arquivo); } catch (IOException ignored) {}
        radiografiaRepository.delete(r);
    }


    @Transactional
    public FichaClinicaResponse criarFichaClinica(Long pacienteId, FichaClinicaRequest req) {
        Paciente paciente = findPaciente(pacienteId);
        FichaClinica f = new FichaClinica();
        f.setPaciente(paciente);
        f.setData(req.data());
        f.setNumeroDente(req.numeroDente());
        f.setObservacoesClinicas(req.observacoesClinicas());
        f.setHistorico(req.historico());
        f.setDeve(req.deve());
        f.setHaver(req.haver());
        return FichaClinicaResponse.from(fichaClinicaRepository.save(f));
    }

    public List<FichaClinicaResponse> listarFichasClinicas(Long pacienteId) {
        findPaciente(pacienteId);
        return fichaClinicaRepository.findByPacienteIdOrderByDataDesc(pacienteId).stream()
                .map(FichaClinicaResponse::from).toList();
    }

    @Transactional
    public FichaClinicaResponse atualizarFichaClinica(Long pacienteId, Long fichaId, FichaClinicaRequest req) {
        findPaciente(pacienteId);
        FichaClinica f = fichaClinicaRepository.findById(fichaId)
                .orElseThrow(() -> new ResourceNotFoundException("Ficha clínica não encontrada com id: " + fichaId));
        f.setData(req.data());
        f.setNumeroDente(req.numeroDente());
        f.setObservacoesClinicas(req.observacoesClinicas());
        f.setHistorico(req.historico());
        f.setDeve(req.deve());
        f.setHaver(req.haver());
        return FichaClinicaResponse.from(fichaClinicaRepository.save(f));
    }

    @Transactional
    public void excluirFichaClinica(Long pacienteId, Long fichaId) {
        findPaciente(pacienteId);
        FichaClinica f = fichaClinicaRepository.findById(fichaId)
                .orElseThrow(() -> new ResourceNotFoundException("Ficha clínica não encontrada com id: " + fichaId));
        fichaClinicaRepository.delete(f);
    }


    @Transactional
    public PlanoTratamentoResponse criarItemPlano(Long pacienteId, PlanoTratamentoRequest req) {
        Paciente paciente = findPaciente(pacienteId);
        PlanoTratamento p = new PlanoTratamento();
        p.setPaciente(paciente);
        p.setProcedimento(req.procedimento());
        p.setNumeroDente(req.numeroDente());
        p.setStatus(req.status() != null ? req.status() : StatusTratamento.PENDENTE);
        p.setObservacoes(req.observacoes());
        p.setValor(req.valor());
        p.setDataPrevista(req.dataPrevista());
        p.setDataConclusao(req.dataConclusao());
        return PlanoTratamentoResponse.from(planoTratamentoRepository.save(p));
    }

    public List<PlanoTratamentoResponse> listarPlanoTratamento(Long pacienteId) {
        findPaciente(pacienteId);
        return planoTratamentoRepository.findByPacienteIdOrderByCreatedAtAsc(pacienteId).stream()
                .map(PlanoTratamentoResponse::from).toList();
    }

    @Transactional
    public PlanoTratamentoResponse atualizarItemPlano(Long pacienteId, Long itemId, PlanoTratamentoRequest req) {
        findPaciente(pacienteId);
        PlanoTratamento p = planoTratamentoRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item do plano não encontrado com id: " + itemId));
        if (!p.getPaciente().getId().equals(pacienteId)) {
            throw new ResourceNotFoundException("Item não pertence a este paciente");
        }
        p.setProcedimento(req.procedimento());
        p.setNumeroDente(req.numeroDente());
        if (req.status() != null) p.setStatus(req.status());
        p.setObservacoes(req.observacoes());
        p.setValor(req.valor());
        p.setDataPrevista(req.dataPrevista());
        p.setDataConclusao(req.dataConclusao());
        return PlanoTratamentoResponse.from(planoTratamentoRepository.save(p));
    }

    @Transactional
    public void excluirItemPlano(Long pacienteId, Long itemId) {
        findPaciente(pacienteId);
        PlanoTratamento p = planoTratamentoRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item do plano não encontrado com id: " + itemId));
        if (!p.getPaciente().getId().equals(pacienteId)) {
            throw new ResourceNotFoundException("Item não pertence a este paciente");
        }
        planoTratamentoRepository.delete(p);
    }


    public List<HistoricoItemResponse> listarHistorico(Long pacienteId) {
        findPaciente(pacienteId);

        List<HistoricoItemResponse> itens = new ArrayList<>();

        anotacaoRepository.findByPacienteIdOrderByDataAnotacaoDesc(pacienteId).forEach(a -> itens.add(
                new HistoricoItemResponse(a.getId(), "ANOTACAO",
                        a.getDataAnotacao() != null ? a.getDataAnotacao() : LocalDate.now(),
                        "Anotação", a.getConteudo(), a.getId())
        ));

        fichaClinicaRepository.findByPacienteIdOrderByDataDesc(pacienteId).forEach(f -> {
            String titulo = f.getNumeroDente() != null
                    ? "Ficha Clínica — Dente " + f.getNumeroDente()
                    : "Ficha Clínica";
            String descricao = f.getObservacoesClinicas() != null ? f.getObservacoesClinicas() : "";
            itens.add(new HistoricoItemResponse(f.getId(), "FICHA_CLINICA", f.getData(), titulo, descricao, f.getId()));
        });

        planoTratamentoRepository.findByPacienteIdOrderByCreatedAtAsc(pacienteId).forEach(p -> {
            LocalDate data = p.getDataConclusao() != null ? p.getDataConclusao()
                    : p.getDataPrevista() != null ? p.getDataPrevista()
                    : LocalDate.now();
            String descricao = p.getStatus().name()
                    + (p.getNumeroDente() != null ? " — Dente " + p.getNumeroDente() : "")
                    + (p.getObservacoes() != null ? ": " + p.getObservacoes() : "");
            itens.add(new HistoricoItemResponse(p.getId(), "PLANO_TRATAMENTO", data, p.getProcedimento(), descricao, p.getId()));
        });

        radiografiaRepository.findByPacienteId(pacienteId).forEach(r -> {
            LocalDate data = r.getDataRealizacao() != null ? r.getDataRealizacao() : LocalDate.now();
            String tipo = r.getTipoRadiografia() != null ? r.getTipoRadiografia().name() : "Radiografia";
            itens.add(new HistoricoItemResponse(r.getId(), "RADIOGRAFIA", data, tipo,
                    r.getDescricao() != null ? r.getDescricao() : r.getNomeOriginal(), r.getId()));
        });

        consultaRepository.findByPacienteIdOrderByDataHoraInicioDesc(pacienteId).forEach(c -> {
            LocalDate data = c.getDataHoraInicio().toLocalDate();
            String titulo = c.getTipo().name();
            String descricao = c.getStatus().name()
                    + (c.getObservacoes() != null ? " — " + c.getObservacoes() : "");
            itens.add(new HistoricoItemResponse(c.getId(), "CONSULTA", data, titulo, descricao, c.getId()));
        });

        lancamentoRepository.findByPacienteIdOrderByDataDesc(pacienteId).forEach(l -> {
            String titulo = l.getTipo().name().equals("RECEITA") ? "Pagamento recebido" : "Lançamento";
            String descricao = l.getDescricao()
                    + (l.getValorPago() != null && l.getValorPago().compareTo(java.math.BigDecimal.ZERO) > 0
                    ? " — Pago: R$ " + l.getValorPago() : "")
                    + (l.getValorTotal() != null && l.getValorTotal().compareTo(java.math.BigDecimal.ZERO) > 0
                    ? " (Total: R$ " + l.getValorTotal() + ")" : "");
            itens.add(new HistoricoItemResponse(l.getId(), "LANCAMENTO", l.getData(), titulo, descricao, l.getId()));
        });

        itens.sort(Comparator.comparing(HistoricoItemResponse::data).reversed());
        return itens;
    }


    @Transactional
    public void excluir(Long id) {
        Paciente paciente = findPaciente(id);
        // Limpa arquivos de radiografia do disco
        radiografiaRepository.findByPacienteId(id).forEach(r -> {
            if (r.getCaminhoArquivo() != null) {
                try { Files.deleteIfExists(Paths.get(uploadDir).resolve(r.getCaminhoArquivo())); }
                catch (IOException ignored) {}
            }
        });
        pacienteRepository.delete(paciente);
    }

    private Paciente findPaciente(Long id) {
        return pacienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente não encontrado com id: " + id));
    }
}
