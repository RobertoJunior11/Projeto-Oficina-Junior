package com.impacta.oficina.service;

import com.impacta.oficina.assembler.VeiculoAssembler;
import com.impacta.oficina.domain.entity.VeiculoEntity;
import com.impacta.oficina.domain.request.VeiculoRequest;
import com.impacta.oficina.domain.response.VeiculoResponse;
import com.impacta.oficina.exceptions.OficinaException;
import com.impacta.oficina.repository.VeiculoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.impacta.oficina.domain.entity.VeiculoEntity.Fields.id;
import static com.impacta.oficina.domain.entity.VeiculoEntity.Fields.marca;
import static com.impacta.oficina.domain.entity.VeiculoEntity.Fields.veiculo;
import static java.lang.Long.parseLong;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class VeiculoService {

    private final VeiculoAssembler assembler;
    private final VeiculoRepository repository;

    /**
     * Cadastra um novo veiculo no sistema.
     * Converte o objeto de request para entity, persiste no banco de dados
     * e retorna a resposta com os dados salvos.
     *
     * @param request dados do veiculo a ser cadastrado
     * @return resposta com os dados do veiculo cadastrado
     */
    public VeiculoResponse cadastrarVeiculo(VeiculoRequest request) {
        VeiculoEntity entity = beforeSaveOrUpdate(request);

        repository.saveAndFlush(entity);

        return assembler.fromEntityToResponse(entity);
    }

    /**
     * Atualiza completamente os dados de um veiculo existente.
     * Substitui todos os campos com os novos valores fornecidos.
     *
     * @param id      identificador do veiculo a ser atualizado
     * @param request novos dados do veiculo
     * @return resposta com os dados atualizados
     * @throws OficinaException se o veiculo não for encontrado
     */
    @Transactional
    public VeiculoResponse updateVeiculo(Long id, VeiculoRequest request) {
        VeiculoEntity entity = fingById(id);

        entity = beforeSaveOrUpdate(request);

        repository.saveAndFlush(entity);

        return assembler.fromEntityToResponse(entity);
    }

    /**
     * Atualiza parcialmente os dados de um veiculo existente.
     * Apenas os campos fornecidos são atualizados, mantendo os demais inalterados.
     *
     * @param id      identificador do veiculo a ser atualizado
     * @param request dados parciais do veiculo
     * @return resposta com os dados atualizados
     * @throws OficinaException se o veiculo não for encontrado
     */
    public VeiculoResponse pathcVeiculo(Long id, VeiculoRequest request) {
        VeiculoEntity entity = fingById(id);

        entity = assembler.fromDTO(request, entity);

        repository.saveAndFlush(entity);

        return assembler.fromEntityToResponse(entity);
    }

    /**
     * Busca um veiculo específico pelo seu identificador.
     *
     * @param id identificador do veiculo
     * @return resposta com os dados do veiculo encontrado
     * @throws OficinaException se o veiculo não for encontrado
     */
    public VeiculoResponse getVeiculo(Long id) {
        VeiculoEntity entity = fingById(id);

        return assembler.fromEntityToResponse(entity);
    }

    /**
     * Busca uma entidade de veiculo pelo ID.
     *
     * @param id identificador do veiculo
     * @return entidade do veiculo encontrado
     * @throws OficinaException se o veiculo não for encontrado
     */
    public VeiculoEntity getVeiculoEntity(Long id) {
        return fingById(id);
    }

    /**
     * Lista todos os veiculos com paginação e filtros opcionais.
     * Permite filtrar por id, nome do veiculo ou marca.
     *
     * @param pageable    configurações de paginação
     * @param queryParams filtros de busca (id, veiculo, marca)
     * @return página com os veiculos encontrados
     */
    public Page<VeiculoResponse> getVeiculos(Pageable pageable, Map<String, String> queryParams) {
        Specification<VeiculoEntity> spec = createSpecification(queryParams);

        Page<VeiculoEntity> veiculos = repository.findAll(spec, pageable);

        List<VeiculoResponse> response = veiculos
                .stream()
                .map(assembler::fromEntityToResponse)
                .collect(Collectors.toList());

        return new PageImpl<>(response, pageable, veiculos.getTotalElements());
    }

    /**
     * Remove um veiculo do sistema pelo seu identificador.
     *
     * @param id identificador do veiculo a ser removido
     */
    public void deletarVeiculo(Long id) {
        repository.deleteById(id);
    }

    /**
     * Converte um request em entity para operações de persistência.
     */
    private VeiculoEntity beforeSaveOrUpdate(VeiculoRequest request) {
        return assembler.fromRequestToEntity(request);
    }

    /**
     * Busca um veiculo pelo ID, lançando exceção se não encontrado.
     */
    protected VeiculoEntity fingById(Long id) {
        log.debug("Buscando veiculo no banco de dados - ID: {}", id);

        return repository.findById(id)
                .orElseThrow(() ->
                        OficinaException.builder()
                                .httpStatusCode(NOT_FOUND)
                                .message("O veiculo não existe")
                                .description("O veiculo informado não existe no sistema")
                                .build()
                );
    }

    /**
     * Cria a especificação de busca baseada nos parâmetros de filtro.
     */
    private Specification<VeiculoEntity> createSpecification(Map<String, String> queryParams) {
        log.debug("Criando specification para filtros - QueryParams: {}", queryParams);

        Specification<VeiculoEntity> spec = null;

        if (queryParams == null) {
            log.debug("Nenhum filtro aplicado");
            return spec;
        }

        for (var entry : queryParams.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            log.debug("Processando filtro - Chave: {}, Valor: {}", key, value);

            Specification<VeiculoEntity> tempSpec = switch (key) {
                case id -> (root, query, cb) -> cb.equal(root.get(id), parseLong(value));
                case veiculo -> (root, query, cb) -> cb.equal(root.get(veiculo), value);
                case marca -> (root, query, cb) -> cb.equal(root.get(marca), value);
                default -> null;
            };

            if (tempSpec != null) {
                spec = (spec == null) ? tempSpec : spec.and(tempSpec);
                log.debug("Filtro adicionado à specification - Chave: {}", key);
            }
        }

        return spec;
    }
}