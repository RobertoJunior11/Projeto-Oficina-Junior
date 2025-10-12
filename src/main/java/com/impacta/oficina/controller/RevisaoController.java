package com.impacta.oficina.controller;

import com.impacta.oficina.domain.request.RevisaoPatchRequest;
import com.impacta.oficina.domain.request.RevisaoRequest;
import com.impacta.oficina.domain.response.RevisaoResponse;
import com.impacta.oficina.service.RevisaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static org.springframework.http.ResponseEntity.noContent;

@Slf4j
@RestController
@RequestMapping("/revisao")
@RequiredArgsConstructor
public class RevisaoController {

    private final RevisaoService service;

    @PostMapping
    public ResponseEntity<RevisaoResponse> cadastrarRevisao(@RequestBody @Valid RevisaoRequest request) {
        log.info("Iniciando cadastro de Revisao - Revisao: {}", request.getId());

        RevisaoResponse response = service.cadastrarRevisao(request);

        log.info("Revisao cadastrado com sucesso - Revisao: {}",
                response.getId());

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<RevisaoResponse>> listarRevisaos(@PageableDefault(size = 1000) Pageable pageable,
                                                                @RequestParam Map<String, String> queryParams) {
        log.info("Iniciando listagem de Revisaos - Página: {}, Tamanho: {}, Filtros: {}",
                pageable.getPageNumber(), pageable.getPageSize(), queryParams);

        Page<RevisaoResponse> response = service.getRevisaos(pageable, queryParams);

        log.info("Listagem de Revisaos concluída - Total de elementos: {}, Total de páginas: {}",
                response.getTotalElements(), response.getTotalPages());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RevisaoResponse> getRevisao(@PathVariable Long id) {
        log.info("Iniciando busca de Revisao por ID: {}", id);

        RevisaoResponse response = service.getRevisao(id);

        log.info("Revisao encontrado - ID: {}", id);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RevisaoResponse> updateRevisao(@PathVariable Long id, @RequestBody @Valid RevisaoRequest request) {
        log.info("Iniciando atualização completa de Revisao - ID: {}", id);

        RevisaoResponse response = service.updateRevisao(id, request);

        log.info("Revisao atualizado com sucesso - ID: {}", id);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<RevisaoResponse> patchRevisao(@PathVariable Long id, @RequestBody @Valid RevisaoPatchRequest request) {
        log.info("Iniciando atualização parcial de Revisao - ID: {}", id);

        RevisaoResponse response = service.pathcRevisao(id, request);

        log.info("Revisao atualizado parcialmente com sucesso - ID: {}", id);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarRevisao(@PathVariable Long id) {
        log.info("Iniciando exclusão de Revisao - ID: {}", id);

        service.deletarRevisao(id);

        log.info("Revisao excluído com sucesso - ID: {}", id);

        return noContent().build();
    }
}
