package br.com.attus.demandas;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DemandaService {
    private static final Logger log = LoggerFactory.getLogger(DemandaService.class);

    private final DemandaRepository repository;

    public DemandaService(DemandaRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<DemandaResponse> listar() {
        return repository.findAll().stream()
                .map(DemandaResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public DemandaResponse buscar(Long id) {
        return DemandaResponse.from(buscarEntidade(id));
    }

    @Transactional
    public DemandaResponse criar(DemandaRequest request) {
        Demanda demanda = repository.save(request.toEntity());
        log.info("demanda_criada id={} prioridade={} responsavel={}", demanda.getId(), demanda.getPrioridade(), demanda.getResponsavel());
        return DemandaResponse.from(demanda);
    }

    @Transactional
    public DemandaResponse atualizar(Long id, DemandaRequest request) {
        Demanda demanda = buscarEntidade(id);
        demanda.atualizar(request);
        log.info("demanda_atualizada id={} status={} responsavel={}", demanda.getId(), demanda.getStatus(), demanda.getResponsavel());
        return DemandaResponse.from(demanda);
    }

    @Transactional
    public DemandaResponse concluir(Long id) {
        Demanda demanda = buscarEntidade(id);
        demanda.concluir();
        log.info("demanda_concluida id={} responsavel={}", demanda.getId(), demanda.getResponsavel());
        return DemandaResponse.from(demanda);
    }

    private Demanda buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Demanda nao encontrada: " + id));
    }
}
