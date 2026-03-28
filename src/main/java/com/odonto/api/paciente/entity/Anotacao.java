package com.odonto.api.paciente.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "anotacoes", indexes = {
        @Index(name = "idx_anotacoes_paciente_id", columnList = "paciente_id"),
        @Index(name = "idx_anotacoes_data", columnList = "data_anotacao")
})
public class Anotacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    private LocalDate dataAnotacao;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String conteudo;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    public Long getId() { return id; }

    public Paciente getPaciente() { return paciente; }
    public void setPaciente(Paciente paciente) { this.paciente = paciente; }

    public LocalDate getDataAnotacao() { return dataAnotacao; }
    public void setDataAnotacao(LocalDate dataAnotacao) { this.dataAnotacao = dataAnotacao; }

    public String getConteudo() { return conteudo; }
    public void setConteudo(String conteudo) { this.conteudo = conteudo; }

    public Instant getCreatedAt() { return createdAt; }
}
