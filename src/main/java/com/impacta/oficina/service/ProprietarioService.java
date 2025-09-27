package com.impacta.oficina.service;

import com.impacta.oficina.assembler.ProprietarioAssembler;
import com.impacta.oficina.domain.entity.ProprietarioEntity;
import com.impacta.oficina.domain.entity.VeiculoEntity;
import com.impacta.oficina.domain.request.ProprietarioPatchRequest;
import com.impacta.oficina.domain.request.ProprietarioRequest;
import com.impacta.oficina.domain.request.VeiculoRequest;
import com.impacta.oficina.domain.response.ProprietarioResponse;
import com.impacta.oficina.exceptions.OficinaException;
import com.impacta.oficina.repository.ProprietarioRepository;
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

import static com.impacta.oficina.domain.entity.ProprietarioEntity.Fields.email;
import static com.impacta.oficina.domain.entity.ProprietarioEntity.Fields.id;
import static com.impacta.oficina.domain.entity.ProprietarioEntity.Fields.nome;
import static com.impacta.oficina.domain.entity.ProprietarioEntity.Fields.telefone;
import static java.lang.Long.parseLong;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProprietarioService {

    private final VeiculoService veiculoService;
    private final ProprietarioAssembler assembler;
    private final ProprietarioRepository repository;

    /**
     * Cadastra um novo Proprietario no sistema.
     * Converte o objeto de request para entity, persiste no banco de dados
     * e retorna a resposta com os dados salvos.
     *
     * @param request dados do Proprietario a ser cadastrado
     * @return resposta com os dados do Proprietario cadastrado
     */
    public ProprietarioResponse cadastrarProprietario(ProprietarioRequest request) {
        ProprietarioEntity entity = beforeSaveOrUpdate(request);

        repository.saveAndFlush(entity);

        return assembler.fromEntityToResponse(entity);
    }

    /**
     * Atualiza completamente os dados de um Proprietario existente.
     * Substitui todos os campos com os novos valores fornecidos.
     *
     * @param id      identificador do Proprietario a ser atualizado
     * @param request novos dados do Proprietario
     * @return resposta com os dados atualizados
     * @throws OficinaException se o Proprietario não for encontrado
     */
    @Transactional
    public ProprietarioResponse updateProprietario(Long id, ProprietarioRequest request) {
        ProprietarioEntity entity = fingById(id);

        entity.setNome(request.getNome());
        entity.setEmail(request.getEmail());
        entity.setTelefone(request.getTelefone());

        repository.saveAndFlush(entity);

        return assembler.fromEntityToResponse(entity);
    }

    /**
     * Atualiza parcialmente os dados de um Proprietario existente.
     * Apenas os campos fornecidos são atualizados, mantendo os demais inalterados.
     *
     * @param id      identificador do Proprietario a ser atualizado
     * @param request dados parciais do Proprietario
     * @return resposta com os dados atualizados
     * @throws OficinaException se o Proprietario não for encontrado
     */
    public ProprietarioResponse pathcProprietario(Long id, ProprietarioPatchRequest request) {
        ProprietarioEntity entity = fingById(id);


        entity = assembler.fromDTO(request, entity);

        repository.saveAndFlush(entity);

        return assembler.fromEntityToResponse(entity);
    }

    /**
     * Busca um Proprietario específico pelo seu identificador.
     *
     * @param id identificador do Proprietario
     * @return resposta com os dados do Proprietario encontrado
     * @throws OficinaException se o Proprietario não for encontrado
     */
    public ProprietarioResponse getProprietario(Long id) {
        ProprietarioEntity entity = fingById(id);

        return assembler.fromEntityToResponse(entity);
    }

    /**
     * Lista todos os Proprietarios com paginação e filtros opcionais.
     * Permite filtrar por id, nome do Proprietario ou marca.
     *
     * @param pageable    configurações de paginação
     * @param queryParams filtros de busca (id, Proprietario, marca)
     * @return página com os Proprietarios encontrados
     */
    public Page<ProprietarioResponse> getProprietarios(Pageable pageable, Map<String, String> queryParams) {
        Specification<ProprietarioEntity> spec = createSpecification(queryParams);

        Page<ProprietarioEntity> Proprietarios = repository.findAll(spec, pageable);

        List<ProprietarioResponse> response = Proprietarios
                .stream()
                .map(assembler::fromEntityToResponse)
                .collect(Collectors.toList());

        return new PageImpl<>(response, pageable, Proprietarios.getTotalElements());
    }

    /**
     * Remove um Proprietario do sistema pelo seu identificador.
     *
     * @param id identificador do Proprietario a ser removido
     */
    public void deletarProprietario(Long id) {
        repository.deleteById(id);
    }

    /**
     * Converte um request em entity para operações de persistência.
     */
    private ProprietarioEntity beforeSaveOrUpdate(ProprietarioRequest request) {
        return assembler.fromRequestToEntity(request);
    }

    /**
     * Busca um Proprietario pelo ID, lançando exceção se não encontrado.
     */
    private ProprietarioEntity fingById(Long id) {
        log.debug("Buscando Proprietario no banco de dados - ID: {}", id);

        return repository.findById(id)
                .orElseThrow(() ->
                        OficinaException.builder()
                                .httpStatusCode(NOT_FOUND)
                                .message("O Proprietario não existe")
                                .description("O Proprietario informado não existe no sistema")
                                .build()
                );
    }

    /**
     * Cria a especificação de busca baseada nos parâmetros de filtro.
     */
    private Specification<ProprietarioEntity> createSpecification(Map<String, String> queryParams) {
        log.debug("Criando specification para filtros - QueryParams: {}", queryParams);

        Specification<ProprietarioEntity> spec = null;

        if (queryParams == null) {
            log.debug("Nenhum filtro aplicado");
            return spec;
        }

        for (var entry : queryParams.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            log.debug("Processando filtro - Chave: {}, Valor: {}", key, value);

            Specification<ProprietarioEntity> tempSpec = switch (key) {
                case id -> (root, query, cb) -> cb.equal(root.get(id), parseLong(value));
                case nome -> (root, query, cb) -> cb.equal(root.get(nome), value);
                case email -> (root, query, cb) -> cb.equal(root.get(email), value);
                case telefone -> (root, query, cb) -> cb.equal(root.get(telefone), value);
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