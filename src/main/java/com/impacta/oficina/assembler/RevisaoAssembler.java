package com.impacta.oficina.assembler;

import com.impacta.oficina.domain.entity.RevisaoEntity;
import com.impacta.oficina.domain.enums.TipoRevisaoEnum;
import com.impacta.oficina.domain.request.RevisaoPatchRequest;
import com.impacta.oficina.domain.request.RevisaoRequest;
import com.impacta.oficina.domain.response.RevisaoResponse;
import com.impacta.oficina.domain.response.TipoRevisaoResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.impacta.oficina.domain.enums.TipoRevisaoEnum.calcularValorTotal;
import static java.util.stream.Collectors.toList;

@Component
@RequiredArgsConstructor
public class RevisaoAssembler {

    private final ModelMapper modelMapper;

    /**
     * Converte um RevisaoRequest em RevisaoEntity.
     *
     * @param request dados de entrada do Revisao
     * @return entidade do Revisao
     */
    public RevisaoEntity fromRequestToEntity(RevisaoRequest request) {
        RevisaoEntity entity = modelMapper.map(request, RevisaoEntity.class);

        if (request.getTiposRevisao() != null) {
            List<TipoRevisaoEnum> tiposEnum = request.getTiposRevisao().stream()
                    .map(codigo -> TipoRevisaoEnum.values()[codigo - 1])
                    .collect(toList());

            entity.setTiposRevisao(tiposEnum);
            entity.setVlServico(calcularValorTotal(request.getTiposRevisao()));
        }

        return entity;
    }


    /**
     * Converte um RevisaoEntity em RevisaoResponse.
     *
     * @param entity entidade do Revisao
     * @return resposta com os dados do Revisao
     */
    public RevisaoResponse fromEntityToResponse(RevisaoEntity entity) {
        RevisaoResponse response = modelMapper.map(entity, RevisaoResponse.class);

        if (entity.getTiposRevisao() != null) {
            List<TipoRevisaoResponse> tiposResponse = entity.getTiposRevisao().stream()
                    .map(tipoEnum -> TipoRevisaoEnum.getResponse(tipoEnum.getId()))
                    .collect(toList());
            response.setTiposRevisao(tiposResponse);
        }

        return response;
    }

    /**
     * Atualiza uma entidade existente com dados de um request.
     * Apenas campos não nulos são atualizados.
     *
     * @param request dados parciais do Revisao
     * @param entity  entidade a ser atualizada
     * @return entidade atualizada
     */
    public RevisaoEntity fromDTO(RevisaoPatchRequest request, RevisaoEntity entity) {
        if (request.getDtRevisao() != null && !entity.getConcluida()) {
            entity.setDtRevisao(request.getDtRevisao());
        }

        if (request.getDescricao() != null) {
            entity.setDescricao(request.getDescricao());
        }

        if (request.getVlServico() != null) {
            entity.setVlServico(request.getVlServico());
        }

        if (request.getTiposRevisao() != null) {
            List<TipoRevisaoEnum> tiposEnum = request.getTiposRevisao().stream()
                    .map(codigo -> TipoRevisaoEnum.values()[codigo - 1])
                    .collect(toList());

            entity.setTiposRevisao(tiposEnum);
        }

        if (request.getConcluida() != null) {
            entity.setConcluida(request.getConcluida());
        }

        return entity;
    }
}
