package com.odonto.api.paciente.entity;

import com.odonto.api.paciente.enums.TipoRadiografia;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "radiografias", indexes = {
        @Index(name = "idx_radiografias_paciente_id", columnList = "paciente_id")
})
public class Radiografia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    private LocalDate dataRealizacao;

    private String descricao;

    private String caminhoArquivo;

    @Enumerated(EnumType.STRING)
    private TipoRadiografia tipoRadiografia;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    public Long getId() { return id; }

    public Paciente getPaciente() { return paciente; }
    public void setPaciente(Paciente paciente) { this.paciente = paciente; }

    public LocalDate getDataRealizacao() { return dataRealizacao; }
    public void setDataRealizacao(LocalDate dataRealizacao) { this.dataRealizacao = dataRealizacao; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getCaminhoArquivo() { return caminhoArquivo; }
    public void setCaminhoArquivo(String caminhoArquivo) { this.caminhoArquivo = caminhoArquivo; }

    public TipoRadiografia getTipoRadiografia() { return tipoRadiografia; }
    public void setTipoRadiografia(TipoRadiografia tipoRadiografia) { this.tipoRadiografia = tipoRadiografia; }

    public Instant getCreatedAt() { return createdAt; }
}
