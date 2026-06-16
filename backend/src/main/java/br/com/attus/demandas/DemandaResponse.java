package br.com.attus.demandas;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public record DemandaResponse(
        Long id,
        String titulo,
        String descricao,
        Prioridade prioridade,
        StatusDemanda status,
        String responsavel,
        LocalDate prazo,
        OffsetDateTime criadoEm,
        OffsetDateTime atualizadoEm
) {
    public static DemandaResponse from(Demanda demanda) {
        return new DemandaResponse(
                demanda.getId(),
                demanda.getTitulo(),
                demanda.getDescricao(),
                demanda.getPrioridade(),
                demanda.getStatus(),
                demanda.getResponsavel(),
                demanda.getPrazo(),
                demanda.getCriadoEm(),
                demanda.getAtualizadoEm()
        );
    }
}
