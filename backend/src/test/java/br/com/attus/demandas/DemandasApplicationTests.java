package br.com.attus.demandas;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DemandasApplicationTests {
    @LocalServerPort
    int port;

    @Autowired
    DemandaRepository repository;

    final TestRestTemplate rest = new TestRestTemplate();

    @BeforeEach
    void cleanDatabase() {
        repository.deleteAll();
    }

    @Test
    void criaEditaEConcluiDemanda() {
        ResponseEntity<Map> criada = rest.postForEntity(url("/api/demandas"), demanda("Inicial", "ABERTA"), Map.class);

        assertThat(criada.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(criada.getBody()).containsEntry("titulo", "Inicial");
        assertThat(criada.getBody()).containsEntry("status", "ABERTA");

        Integer id = (Integer) criada.getBody().get("id");
        rest.put(url("/api/demandas/" + id), demanda("Revisada", "EM_ANDAMENTO"));

        ResponseEntity<Map> editada = rest.getForEntity(url("/api/demandas/" + id), Map.class);
        assertThat(editada.getBody()).containsEntry("titulo", "Revisada");
        assertThat(editada.getBody()).containsEntry("status", "EM_ANDAMENTO");

        ResponseEntity<Map> concluida = rest.exchange(url("/api/demandas/" + id + "/concluir"), HttpMethod.PATCH, null, Map.class);
        assertThat(concluida.getBody()).containsEntry("status", "CONCLUIDA");
    }

    @Test
    void rejeitaDemandaInvalida() {
        Map<String, Object> invalida = demanda("", "ABERTA");
        invalida.put("prazo", LocalDate.now().minusDays(1).toString());

        ResponseEntity<Map> response = rest.postForEntity(url("/api/demandas"), invalida, Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsEntry("message", "Dados invalidos");
        Map<String, Object> fields = (Map<String, Object>) response.getBody().get("fields");
        assertThat(fields).containsKeys("titulo", "prazo");
    }

    private Map<String, Object> demanda(String titulo, String status) {
        Map<String, Object> demanda = new LinkedHashMap<>();
        demanda.put("titulo", titulo);
        demanda.put("descricao", "Analisar movimentacao processual e registrar encaminhamento.");
        demanda.put("prioridade", "ALTA");
        demanda.put("status", status);
        demanda.put("responsavel", "Marina Costa");
        demanda.put("prazo", LocalDate.now().plusDays(5).toString());
        return demanda;
    }

    private String url(String path) {
        return "http://localhost:" + port + path;
    }
}
