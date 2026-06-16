package br.com.attus.demandas;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record DemandaRequest(
        @NotBlank @Size(max = 120) String titulo,
        @NotBlank @Size(max = 1000) String descricao,
        @NotNull Prioridade prioridade,
        @NotNull StatusDemanda status,
        @NotBlank @Size(max = 80) String responsavel,
        @NotNull @FutureOrPresent LocalDate prazo
) {
    public Demanda toEntity() {
        Demanda demanda = new Demanda(titulo, descricao, prioridade, responsavel, prazo);
        demanda.atualizar(this);
        return demanda;
    }
}
