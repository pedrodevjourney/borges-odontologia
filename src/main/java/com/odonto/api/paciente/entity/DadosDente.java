package com.odonto.api.paciente.entity;

import jakarta.persistence.*;

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

    private String cor;

    private String escurecimento;

    private String forma;

    private String observacoes;

    public Long getId() { return id; }

    public Paciente getPaciente() { return paciente; }
    public void setPaciente(Paciente paciente) { this.paciente = paciente; }

    public Integer getNumeroDente() { return numeroDente; }
    public void setNumeroDente(Integer numeroDente) { this.numeroDente = numeroDente; }

    public String getCor() { return cor; }
    public void setCor(String cor) { this.cor = cor; }

    public String getEscurecimento() { return escurecimento; }
    public void setEscurecimento(String escurecimento) { this.escurecimento = escurecimento; }

    public String getForma() { return forma; }
    public void setForma(String forma) { this.forma = forma; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
}
