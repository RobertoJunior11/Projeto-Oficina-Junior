package com.impacta.oficina.controller;

import com.impacta.oficina.domain.request.VeiculoRequest;
import com.impacta.oficina.domain.response.VeiculoResponse;
import com.impacta.oficina.service.VeiculoService;
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
@RequestMapping("/veiculo")
@RequiredArgsConstructor
public class VeiculoController {

    private final VeiculoService service;

    @PostMapping
    public ResponseEntity<VeiculoResponse> cadastrarVeiculo(@RequestBody @Valid VeiculoRequest request) {
        log.info("Iniciando cadastro de Veiculo - Veiculo: {}, Marca: {}", request.getVeiculo(), request.getMarca());

        VeiculoResponse response = service.cadastrarVeiculo(request);

        log.info("Veiculo cadastrado com sucesso - ID: {}, Veiculo: {}, Marca: {}",
                response.getId(), response.getVeiculo(), response.getMarca());

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<VeiculoResponse>> listarVeiculos(@PageableDefault(size = 1000) Pageable pageable,
                                                                @RequestParam Map<String, String> queryParams) {
        log.info("Iniciando listagem de Veiculos - Página: {}, Tamanho: {}, Filtros: {}",
                pageable.getPageNumber(), pageable.getPageSize(), queryParams);

        Page<VeiculoResponse> response = service.getVeiculos(pageable, queryParams);

        log.info("Listagem de Veiculos concluída - Total de elementos: {}, Total de páginas: {}",
                response.getTotalElements(), response.getTotalPages());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VeiculoResponse> getVeiculo(@PathVariable Long id) {
        log.info("Iniciando busca de Veiculo por ID: {}", id);

        VeiculoResponse response = service.getVeiculo(id);

        log.info("Veiculo encontrado - ID: {}, Veiculo: {}, Marca: {}",
                response.getId(), response.getVeiculo(), response.getMarca());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VeiculoResponse> updateVeiculo(@PathVariable Long id, @RequestBody @Valid VeiculoRequest request) {
        log.info("Iniciando atualização completa de Veiculo - ID: {}, Veiculo: {}, Marca: {}",
                id, request.getVeiculo(), request.getMarca());

        VeiculoResponse response = service.updateVeiculo(id, request);

        log.info("Veiculo atualizado com sucesso - ID: {}, Veiculo: {}, Marca: {}",
                response.getId(), response.getVeiculo(), response.getMarca());

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<VeiculoResponse> patchVeiculo(@PathVariable Long id, @RequestBody @Valid VeiculoRequest request) {
        log.info("Iniciando atualização parcial de Veiculo - ID: {}, Dados: {}", id, request);

        VeiculoResponse response = service.pathcVeiculo(id, request);

        log.info("Veiculo atualizado parcialmente com sucesso - ID: {}, Veiculo: {}, Marca: {}",
                response.getId(), response.getVeiculo(), response.getMarca());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarVeiculo(@PathVariable Long id) {
        log.info("Iniciando exclusão de Veiculo - ID: {}", id);

        service.deletarVeiculo(id);

        log.info("Veiculo excluído com sucesso - ID: {}", id);

        return noContent().build();
    }
}
