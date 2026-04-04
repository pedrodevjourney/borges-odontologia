package com.odonto.api.paciente.service;

import com.odonto.api.paciente.dto.*;
import com.odonto.api.paciente.entity.*;
import com.odonto.api.exception.ResourceNotFoundException;
import com.odonto.api.paciente.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class PacienteService {

    private final PacienteRepository pacienteRepository;
    private final DadosDenteRepository dadosDenteRepository;
    private final AnotacaoRepository anotacaoRepository;
    private final RadiografiaRepository radiografiaRepository;
    private final FichaClinicaRepository fichaClinicaRepository;

    public PacienteService(PacienteRepository pacienteRepository,
                           DadosDenteRepository dadosDenteRepository,
                           AnotacaoRepository anotacaoRepository,
                           RadiografiaRepository radiografiaRepository,
                           FichaClinicaRepository fichaClinicaRepository) {
        this.pacienteRepository = pacienteRepository;
        this.dadosDenteRepository = dadosDenteRepository;
        this.anotacaoRepository = anotacaoRepository;
        this.radiografiaRepository = radiografiaRepository;
        this.fichaClinicaRepository = fichaClinicaRepository;
    }


    @Transactional
    public PacienteResponse criar(PacienteRequest req) {
        if (req.numero() != null && pacienteRepository.findByNumero(req.numero()).isPresent()) {
            throw new IllegalStateException("Já existe um paciente cadastrado com o número " + req.numero());
        }
        Paciente p = new Paciente();
        p.setNumero(req.numero());
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
        p.setDlne(req.dlne());
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
    public DadosDenteResponse criarDadosDente(Long pacienteId, DadosDenteRequest req) {
        Paciente paciente = findPaciente(pacienteId);
        DadosDente d = new DadosDente();
        d.setPaciente(paciente);
        d.setNumeroDente(req.numeroDente());
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
    public RadiografiaResponse criarRadiografia(Long pacienteId, RadiografiaRequest req) {
        Paciente paciente = findPaciente(pacienteId);
        Radiografia r = new Radiografia();
        r.setPaciente(paciente);
        r.setDataRealizacao(req.dataRealizacao());
        r.setDescricao(req.descricao());
        r.setCaminhoArquivo(req.caminhoArquivo());
        r.setTipoRadiografia(req.tipoRadiografia());
        return RadiografiaResponse.from(radiografiaRepository.save(r));
    }

    public List<RadiografiaResponse> listarRadiografias(Long pacienteId) {
        findPaciente(pacienteId);
        return radiografiaRepository.findByPacienteId(pacienteId).stream()
                .map(RadiografiaResponse::from).toList();
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
    public void excluir(Long id) {
        Paciente paciente = findPaciente(id);
        pacienteRepository.delete(paciente);
    }

    private Paciente findPaciente(Long id) {
        return pacienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente não encontrado com id: " + id));
    }
}
