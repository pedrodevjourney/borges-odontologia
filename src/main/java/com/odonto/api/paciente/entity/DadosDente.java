package com.odonto.api.paciente.entity;

import com.odonto.api.paciente.enums.StatusDente;
import jakarta.persistence.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(name = "dados_dentes", indexes = {
        @Index(name = "idx_dados_dentes_paciente_id", columnList = "paciente_id")
})
public class DadosDente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @Column(nullable = false)
    private Integer numeroDente;

    @Enumerated(EnumType.STRING)
    private StatusDente status;

    private String cor;

    private String escurecimento;

    private String forma;

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;

    public Long getId() { return id; }

    public Paciente getPaciente() { return paciente; }
    public void setPaciente(Paciente paciente) { this.paciente = paciente; }

    public Integer getNumeroDente() { return numeroDente; }
    public void setNumeroDente(Integer numeroDente) { this.numeroDente = numeroDente; }

    public StatusDente getStatus() { return status; }
    public void setStatus(StatusDente status) { this.status = status; }

    public String getCor() { return cor; }
    public void setCor(String cor) { this.cor = cor; }

    public String getEscurecimento() { return escurecimento; }
    public void setEscurecimento(String escurecimento) { this.escurecimento = escurecimento; }

    public String getForma() { return forma; }
    public void setForma(String forma) { this.forma = forma; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    public Instant getUpdatedAt() { return updatedAt; }
}
