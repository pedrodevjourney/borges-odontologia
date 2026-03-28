package com.odonto.api.paciente.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "fichas_clinicas", indexes = {
        @Index(name = "idx_fichas_clinicas_paciente_id", columnList = "paciente_id"),
        @Index(name = "idx_fichas_clinicas_data", columnList = "data")
})
public class FichaClinica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @Column(nullable = false)
    private LocalDate data;

    private Integer numeroDente;

    @Column(columnDefinition = "TEXT")
    private String observacoesClinicas;

    @Column(columnDefinition = "TEXT")
    private String historico;

    @Column(precision = 10, scale = 2)
    private BigDecimal deve;

    @Column(precision = 10, scale = 2)
    private BigDecimal haver;

    @Column(precision = 10, scale = 2)
    private BigDecimal saldo;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    @PreUpdate
    public void calcularSaldo() {
        BigDecimal valorDeve = this.deve != null ? this.deve : BigDecimal.ZERO;
        BigDecimal valorHaver = this.haver != null ? this.haver : BigDecimal.ZERO;
        this.saldo = valorHaver.subtract(valorDeve);
    }

    public Long getId() { return id; }
    public Paciente getPaciente() { return paciente; }
    public void setPaciente(Paciente paciente) { this.paciente = paciente; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public Integer getNumeroDente() { return numeroDente; }
    public void setNumeroDente(Integer numeroDente) { this.numeroDente = numeroDente; }

    public String getObservacoesClinicas() { return observacoesClinicas; }
    public void setObservacoesClinicas(String observacoesClinicas) { this.observacoesClinicas = observacoesClinicas; }

    public String getHistorico() { return historico; }
    public void setHistorico(String historico) { this.historico = historico; }

    public BigDecimal getDeve() { return deve; }
    public void setDeve(BigDecimal deve) { this.deve = deve; }

    public BigDecimal getHaver() { return haver; }
    public void setHaver(BigDecimal haver) { this.haver = haver; }

    public BigDecimal getSaldo() { return saldo; }
    public Instant getCreatedAt() { return createdAt; }
}
