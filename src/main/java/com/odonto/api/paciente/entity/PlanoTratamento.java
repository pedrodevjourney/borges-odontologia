package com.odonto.api.paciente.entity;

import com.odonto.api.paciente.enums.StatusTratamento;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "planos_tratamento", indexes = {
        @Index(name = "idx_planos_tratamento_paciente_id", columnList = "paciente_id")
})
public class PlanoTratamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @Column(nullable = false)
    private String procedimento;

    private Integer numeroDente;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusTratamento status = StatusTratamento.PENDENTE;

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    @Column(precision = 10, scale = 2)
    private BigDecimal valor;

    private LocalDate dataPrevista;

    private LocalDate dataConclusao;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;

    public Long getId() { return id; }

    public Paciente getPaciente() { return paciente; }
    public void setPaciente(Paciente paciente) { this.paciente = paciente; }

    public String getProcedimento() { return procedimento; }
    public void setProcedimento(String procedimento) { this.procedimento = procedimento; }

    public Integer getNumeroDente() { return numeroDente; }
    public void setNumeroDente(Integer numeroDente) { this.numeroDente = numeroDente; }

    public StatusTratamento getStatus() { return status; }
    public void setStatus(StatusTratamento status) { this.status = status; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }

    public LocalDate getDataPrevista() { return dataPrevista; }
    public void setDataPrevista(LocalDate dataPrevista) { this.dataPrevista = dataPrevista; }

    public LocalDate getDataConclusao() { return dataConclusao; }
    public void setDataConclusao(LocalDate dataConclusao) { this.dataConclusao = dataConclusao; }

    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
