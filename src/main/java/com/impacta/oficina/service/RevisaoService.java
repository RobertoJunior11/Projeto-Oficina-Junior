package com.impacta.oficina.service;

import com.impacta.oficina.assembler.RevisaoAssembler;
import com.impacta.oficina.domain.entity.RevisaoEntity;
import com.impacta.oficina.domain.entity.VeiculoEntity;
import com.impacta.oficina.domain.enums.TipoRevisaoEnum;
import com.impacta.oficina.domain.request.RevisaoPatchRequest;
import com.impacta.oficina.domain.request.RevisaoRequest;
import com.impacta.oficina.domain.request.VeiculoRequest;
import com.impacta.oficina.domain.response.RevisaoResponse;
import com.impacta.oficina.exceptions.OficinaException;
import com.impacta.oficina.repository.RevisaoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.impacta.oficina.domain.entity.GenericEntity.Fields.dtAtualizacao;
import static com.impacta.oficina.domain.entity.GenericEntity.Fields.dtCriacao;
import static com.impacta.oficina.domain.entity.RevisaoEntity.Fields.concluida;
import static com.impacta.oficina.domain.entity.RevisaoEntity.Fields.descricao;
import static com.impacta.oficina.domain.entity.RevisaoEntity.Fields.dtRevisao;
import static com.impacta.oficina.domain.entity.RevisaoEntity.Fields.id;
import static com.impacta.oficina.domain.entity.RevisaoEntity.Fields.vlServico;
import static com.impacta.oficina.domain.entity.VeiculoEntity.Fields.ano;
import static com.impacta.oficina.domain.entity.VeiculoEntity.Fields.marca;
import static com.impacta.oficina.domain.entity.VeiculoEntity.Fields.veiculo;
import static java.lang.Boolean.parseBoolean;
import static java.lang.Long.parseLong;
import static java.time.LocalDateTime.parse;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class RevisaoService {

    private final VeiculoService veiculoService;
    private final RevisaoAssembler assembler;
    private final RevisaoRepository repository;

    /**
     * Cadastra um novo Revisao no sistema.
     * Converte o objeto de request para entity, persiste no banco de dados
     * e retorna a resposta com os dados salvos.
     *
     * @param request dados do Revisao a ser cadastrado
     * @return resposta com os dados do Revisao cadastrado
     */
    public RevisaoResponse cadastrarRevisao(RevisaoRequest request) {
        RevisaoEntity entity = beforeSaveOrUpdate(request);

        repository.saveAndFlush(entity);

        return assembler.fromEntityToResponse(entity);
    }

    /**
     * Atualiza completamente os dados de um Revisao existente.
     * Substitui todos os campos com os novos valores fornecidos.
     *
     * @param id      identificador do Revisao a ser atualizado
     * @param request novos dados do Revisao
     * @return resposta com os dados atualizados
     * @throws OficinaException se o Revisao não for encontrado
     */
    @Transactional
    public RevisaoResponse updateRevisao(Long id, RevisaoRequest request) {
        RevisaoEntity entity = fingById(id);

        validarVeiculo(request.getVeiculo());

        entity = beforeSaveOrUpdate(request);

        repository.saveAndFlush(entity);

        return assembler.fromEntityToResponse(entity);
    }

    /**
     * Atualiza parcialmente os dados de um Revisao existente.
     * Apenas os campos fornecidos são atualizados, mantendo os demais inalterados.
     *
     * @param id      identificador do Revisao a ser atualizado
     * @param request dados parciais do Revisao
     * @return resposta com os dados atualizados
     * @throws OficinaException se o Revisao não for encontrado
     */
    public RevisaoResponse pathcRevisao(Long id, RevisaoPatchRequest request) {
        RevisaoEntity entity = fingById(id);

        entity = assembler.fromDTO(request, entity);

        repository.saveAndFlush(entity);

        return assembler.fromEntityToResponse(entity);
    }

    /**
     * Busca um Revisao específico pelo seu identificador.
     *
     * @param id identificador do Revisao
     * @return resposta com os dados do Revisao encontrado
     * @throws OficinaException se o Revisao não for encontrado
     */
    public RevisaoResponse getRevisao(Long id) {
        RevisaoEntity entity = fingById(id);

        return assembler.fromEntityToResponse(entity);
    }

    /**
     * Lista todos os Revisaos com paginação e filtros opcionais.
     * Permite filtrar por id, nome do Revisao ou marca.
     *
     * @param pageable    configurações de paginação
     * @param queryParams filtros de busca (id, Revisao, marca)
     * @return página com os Revisaos encontrados
     */
    public Page<RevisaoResponse> getRevisaos(Pageable pageable, Map<String, String> queryParams) {
        Specification<RevisaoEntity> spec = createSpecification(queryParams);

        Page<RevisaoEntity> Revisaos = repository.findAll(spec, pageable);

        List<RevisaoResponse> response = Revisaos
                .stream()
                .map(assembler::fromEntityToResponse)
                .collect(Collectors.toList());

        return new PageImpl<>(response, pageable, Revisaos.getTotalElements());
    }

    /**
     * Remove um Revisao do sistema pelo seu identificador.
     *
     * @param id identificador do Revisao a ser removido
     */
    public void deletarRevisao(Long id) {
        repository.deleteById(id);
    }

    /**
     * Converte um request em entity para operações de persistência.
     */
    private RevisaoEntity beforeSaveOrUpdate(RevisaoRequest request) {
        return assembler.fromRequestToEntity(request);
    }

    /**
     * Busca um Revisao pelo ID, lançando exceção se não encontrado.
     */
    private RevisaoEntity fingById(Long id) {
        log.debug("Buscando Revisao no banco de dados - ID: {}", id);

        return repository.findById(id)
                .orElseThrow(() ->
                        OficinaException.builder()
                                .httpStatusCode(NOT_FOUND)
                                .message("O Revisao não existe")
                                .description("O Revisao informado não existe no sistema")
                                .build()
                );
    }

    /**
     * Cria a especificação de busca baseada nos parâmetros de filtro.
     */
    private Specification<RevisaoEntity> createSpecification(Map<String, String> queryParams) {
        log.debug("Criando specification para filtros - QueryParams: {}", queryParams);

        Specification<RevisaoEntity> spec = null;

        if (queryParams == null) {
            log.debug("Nenhum filtro aplicado");
            return spec;
        }

        for (var entry : queryParams.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            log.debug("Processando filtro - Chave: {}, Valor: {}", key, value);

            Specification<RevisaoEntity> tempSpec = switch (key) {
                case id -> (root, query, cb) -> cb.equal(root.get(id), parseLong(value));
                case dtRevisao -> (root, query, cb) -> cb.equal(root.get(dtRevisao), parse(value));
                case descricao -> (root, query, cb) -> cb.equal(root.get(descricao), value);
                case vlServico -> (root, query, cb) -> cb.equal(root.get(vlServico), new BigDecimal(value));
                case concluida -> (root, query, cb) -> cb.equal(root.get(concluida), parseBoolean(value));
                case dtCriacao -> (root, query, cb) -> cb.equal(root.get(dtCriacao), parse(value));
                case dtAtualizacao -> (root, query, cb) -> cb.equal(root.get(dtAtualizacao), parse(value));
                case veiculo -> (root, query, cb) -> {
                    var veiculoJoin = root.join(veiculo);
                    return cb.like(cb.lower(veiculoJoin.get(veiculo)),
                            "%" + value.toLowerCase() + "%");
                };
                case marca -> (root, query, cb) -> {
                    var veiculoJoin = root.join(veiculo);
                    return cb.like(cb.lower(veiculoJoin.get(marca)),
                            "%" + value.toLowerCase() + "%");
                };
                case ano -> (root, query, cb) -> {
                    var veiculoJoin = root.join(veiculo);
                    return cb.equal(veiculoJoin.get(ano), Integer.parseInt(value));
                };
                default -> null;
            };

            if (tempSpec != null) {
                spec = (spec == null) ? tempSpec : spec.and(tempSpec);
                log.debug("Filtro adicionado à specification - Chave: {}", key);
            }
        }

        return spec;
    }

    private void validarVeiculo(VeiculoRequest veiculoRequest) {
        VeiculoEntity veiculo = veiculoService.fingById(veiculoRequest.getId());

        if (veiculo.getProprietario() == null) {
            throw OficinaException.builder()
                    .httpStatusCode(BAD_REQUEST)
                    .message("Veículo sem proprietário vinculado")
                    .description("Um veículo sem proprietário vinculado não pode agendar uma revisão")
                    .build();
        }
    }
}