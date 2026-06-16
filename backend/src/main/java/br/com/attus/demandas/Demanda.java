package br.com.attus.demandas;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "demandas")
public class Demanda {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String titulo;

    @Column(nullable = false, length = 1000)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Prioridade prioridade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusDemanda status;

    @Column(nullable = false, length = 80)
    private String responsavel;

    @Column(nullable = false)
    private LocalDate prazo;

    @Column(nullable = false, updatable = false)
    private OffsetDateTime criadoEm = OffsetDateTime.now();

    @Column(nullable = false)
    private OffsetDateTime atualizadoEm = OffsetDateTime.now();

    protected Demanda() {
    }

    public Demanda(String titulo, String descricao, Prioridade prioridade, String responsavel, LocalDate prazo) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.prioridade = prioridade;
        this.status = StatusDemanda.ABERTA;
        this.responsavel = responsavel;
        this.prazo = prazo;
    }

    public void atualizar(DemandaRequest request) {
        this.titulo = request.titulo();
        this.descricao = request.descricao();
        this.prioridade = request.prioridade();
        this.status = request.status();
        this.responsavel = request.responsavel();
        this.prazo = request.prazo();
        this.atualizadoEm = OffsetDateTime.now();
    }

    public void concluir() {
        this.status = StatusDemanda.CONCLUIDA;
        this.atualizadoEm = OffsetDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public Prioridade getPrioridade() {
        return prioridade;
    }

    public StatusDemanda getStatus() {
        return status;
    }

    public String getResponsavel() {
        return responsavel;
    }

    public LocalDate getPrazo() {
        return prazo;
    }

    public OffsetDateTime getCriadoEm() {
        return criadoEm;
    }

    public OffsetDateTime getAtualizadoEm() {
        return atualizadoEm;
    }
}
