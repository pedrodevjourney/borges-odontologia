package com.odonto.api.paciente.entity;

import com.odonto.api.paciente.enums.EstadoCivil;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pacientes", indexes = {
        @Index(name = "idx_pacientes_nome", columnList = "nome"),
        @Index(name = "idx_pacientes_numero", columnList = "numero")
})
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private Integer numero;

    @Column(nullable = false)
    private String nome;

    private String residencia;

    private String enderecoCompleto;

    private String profissao;

    private LocalDate dataNascimento;

    private String nacionalidade;

    private String indicadoPor;

    private LocalDate inicioTratamento;

    private LocalDate terminoTratamento;

    private LocalDate interrupcaoTratamento;

    private String telefone;

    private String telefoneSecundario;

    @Enumerated(EnumType.STRING)
    private EstadoCivil estadoCivil;

    private Boolean dlne;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;

    @OneToMany(mappedBy = "paciente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DadosDente> dadosDentes = new ArrayList<>();

    @OneToMany(mappedBy = "paciente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Anotacao> anotacoes = new ArrayList<>();

    @OneToMany(mappedBy = "paciente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Radiografia> radiografias = new ArrayList<>();

    @OneToMany(mappedBy = "paciente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FichaClinica> fichasClinicas = new ArrayList<>();

    public Long getId() { return id; }

    public Integer getNumero() { return numero; }
    public void setNumero(Integer numero) { this.numero = numero; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getResidencia() { return residencia; }
    public void setResidencia(String residencia) { this.residencia = residencia; }

    public String getEnderecoCompleto() { return enderecoCompleto; }
    public void setEnderecoCompleto(String enderecoCompleto) { this.enderecoCompleto = enderecoCompleto; }

    public String getProfissao() { return profissao; }
    public void setProfissao(String profissao) { this.profissao = profissao; }

    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }

    public String getNacionalidade() { return nacionalidade; }
    public void setNacionalidade(String nacionalidade) { this.nacionalidade = nacionalidade; }

    public String getIndicadoPor() { return indicadoPor; }
    public void setIndicadoPor(String indicadoPor) { this.indicadoPor = indicadoPor; }

    public LocalDate getInicioTratamento() { return inicioTratamento; }
    public void setInicioTratamento(LocalDate inicioTratamento) { this.inicioTratamento = inicioTratamento; }

    public LocalDate getTerminoTratamento() { return terminoTratamento; }
    public void setTerminoTratamento(LocalDate terminoTratamento) { this.terminoTratamento = terminoTratamento; }

    public LocalDate getInterrupcaoTratamento() { return interrupcaoTratamento; }
    public void setInterrupcaoTratamento(LocalDate interrupcaoTratamento) { this.interrupcaoTratamento = interrupcaoTratamento; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getTelefoneSecundario() { return telefoneSecundario; }
    public void setTelefoneSecundario(String telefoneSecundario) { this.telefoneSecundario = telefoneSecundario; }

    public EstadoCivil getEstadoCivil() { return estadoCivil; }
    public void setEstadoCivil(EstadoCivil estadoCivil) { this.estadoCivil = estadoCivil; }

    public Boolean getDlne() { return dlne; }
    public void setDlne(Boolean dlne) { this.dlne = dlne; }

    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    public List<DadosDente> getDadosDentes() { return dadosDentes; }
    public void setDadosDentes(List<DadosDente> dadosDentes) { this.dadosDentes = dadosDentes; }

    public List<Anotacao> getAnotacoes() { return anotacoes; }
    public void setAnotacoes(List<Anotacao> anotacoes) { this.anotacoes = anotacoes; }

    public List<Radiografia> getRadiografias() { return radiografias; }
    public void setRadiografias(List<Radiografia> radiografias) { this.radiografias = radiografias; }

    public List<FichaClinica> getFichasClinicas() { return fichasClinicas; }
    public void setFichasClinicas(List<FichaClinica> fichasClinicas) { this.fichasClinicas = fichasClinicas; }
}
