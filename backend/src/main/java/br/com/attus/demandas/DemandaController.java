package br.com.attus.demandas;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/demandas")
public class DemandaController {
    private final DemandaService service;

    public DemandaController(DemandaService service) {
        this.service = service;
    }

    @GetMapping
    public List<DemandaResponse> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public DemandaResponse buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DemandaResponse criar(@Valid @RequestBody DemandaRequest request) {
        return service.criar(request);
    }

    @PutMapping("/{id}")
    public DemandaResponse atualizar(@PathVariable Long id, @Valid @RequestBody DemandaRequest request) {
        return service.atualizar(id, request);
    }

    @PatchMapping("/{id}/concluir")
    public DemandaResponse concluir(@PathVariable Long id) {
        return service.concluir(id);
    }
}
