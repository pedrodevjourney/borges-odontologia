package com.odonto.api.financeiro.entity;

import com.odonto.api.financeiro.enums.TipoLancamento;
import com.odonto.api.paciente.entity.Paciente;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "lancamentos", indexes = {
        @Index(name = "idx_lancamentos_paciente_id", columnList = "paciente_id"),
        @Index(name = "idx_lancamentos_data", columnList = "data"),
        @Index(name = "idx_lancamentos_tipo", columnList = "tipo")
})
public class Lancamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoLancamento tipo;

    @Column(nullable = false)
    private String descricao;

    @Column(nullable = false)
    private LocalDate data;

    @Column(precision = 10, scale = 2)
    private BigDecimal deve;

    @Column(precision = 10, scale = 2)
    private BigDecimal haver;

    @Column(precision = 10, scale = 2)
    private BigDecimal saldo;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;

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

    public TipoLancamento getTipo() { return tipo; }
    public void setTipo(TipoLancamento tipo) { this.tipo = tipo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public BigDecimal getDeve() { return deve; }
    public void setDeve(BigDecimal deve) { this.deve = deve; }

    public BigDecimal getHaver() { return haver; }
    public void setHaver(BigDecimal haver) { this.haver = haver; }

    public BigDecimal getSaldo() { return saldo; }

    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
