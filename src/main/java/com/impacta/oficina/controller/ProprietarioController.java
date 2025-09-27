package com.impacta.oficina.controller;

import com.impacta.oficina.domain.request.ProprietarioPatchRequest;
import com.impacta.oficina.domain.request.ProprietarioRequest;
import com.impacta.oficina.domain.response.ProprietarioResponse;
import com.impacta.oficina.service.ProprietarioService;
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
@RequestMapping("/proprietario")
@RequiredArgsConstructor
public class ProprietarioController {

    private final ProprietarioService service;

    @PostMapping
    public ResponseEntity<ProprietarioResponse> cadastrarProprietario(@RequestBody @Valid ProprietarioRequest request) {
        log.info("Iniciando cadastro de Proprietario - Proprietario: {}", request.getNome());

        ProprietarioResponse response = service.cadastrarProprietario(request);

        log.info("Proprietario cadastrado com sucesso - Proprietario: {}",
                response.getNome());

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<ProprietarioResponse>> listarProprietarios(@PageableDefault(size = 1000) Pageable pageable,
                                                                          @RequestParam Map<String, String> queryParams) {
        log.info("Iniciando listagem de Proprietarios - Página: {}, Tamanho: {}, Filtros: {}",
                pageable.getPageNumber(), pageable.getPageSize(), queryParams);

        Page<ProprietarioResponse> response = service.getProprietarios(pageable, queryParams);

        log.info("Listagem de Proprietarios concluída - Total de elementos: {}, Total de páginas: {}",
                response.getTotalElements(), response.getTotalPages());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProprietarioResponse> getProprietario(@PathVariable Long id) {
        log.info("Iniciando busca de Proprietario por ID: {}", id);

        ProprietarioResponse response = service.getProprietario(id);

        log.info("Proprietario encontrado - ID: {}", id);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProprietarioResponse> updateProprietario(@PathVariable Long id, @RequestBody @Valid ProprietarioRequest request) {
        log.info("Iniciando atualização completa de Proprietario - ID: {}", id);

        ProprietarioResponse response = service.updateProprietario(id, request);

        log.info("Proprietario atualizado com sucesso - ID: {}", id);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProprietarioResponse> patchProprietario(@PathVariable Long id, @RequestBody @Valid ProprietarioPatchRequest request) {
        log.info("Iniciando atualização parcial de Proprietario - ID: {}", id);

        ProprietarioResponse response = service.pathcProprietario(id, request);

        log.info("Proprietario atualizado parcialmente com sucesso - ID: {}", id);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarProprietario(@PathVariable Long id) {
        log.info("Iniciando exclusão de Proprietario - ID: {}", id);

        service.deletarProprietario(id);

        log.info("Proprietario excluído com sucesso - ID: {}", id);

        return noContent().build();
    }
}
