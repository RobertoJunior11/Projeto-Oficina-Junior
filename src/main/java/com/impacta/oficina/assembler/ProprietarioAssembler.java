package com.impacta.oficina.assembler;

import com.impacta.oficina.domain.entity.ProprietarioEntity;
import com.impacta.oficina.domain.entity.VeiculoEntity;
import com.impacta.oficina.domain.request.ProprietarioPatchRequest;
import com.impacta.oficina.domain.request.ProprietarioRequest;
import com.impacta.oficina.domain.response.ProprietarioResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
@RequiredArgsConstructor
public class ProprietarioAssembler {

    private final ModelMapper modelMapper;
    private final VeiculoAssembler veiculoAssembler;

    public VeiculoAssembler getVeiculoAssembler() {
        return veiculoAssembler;
    }

    /**
     * Converte um ProprietarioRequest em ProprietarioEntity.
     *
     * @param request dados de entrada do proprietario
     * @return entidade do proprietario
     */
    public ProprietarioEntity fromRequestToEntity(ProprietarioRequest request) {
        return modelMapper.map(request, ProprietarioEntity.class);
    }

    /**
     * Converte um ProprietarioEntity em ProprietarioResponse.
     *
     * @param entity entidade do proprietario
     * @return resposta com os dados do proprietario
     */
    public ProprietarioResponse fromEntityToResponse(ProprietarioEntity entity) {
        return modelMapper.map(entity, ProprietarioResponse.class);
    }

    /**
     * Atualiza uma entidade existente com dados de um request.
     * Apenas campos não nulos são atualizados.
     *
     * @param request dados parciais do proprietario
     * @param entity  entidade a ser atualizada
     * @return entidade atualizada
     */
    public ProprietarioEntity fromDTO(ProprietarioPatchRequest request, ProprietarioEntity entity) {
        if (request.getVeiculos() != null && !request.getVeiculos().isEmpty()) {
            if (entity.getVeiculos() == null) {
                entity.setVeiculos(new ArrayList<>());
            }

            List<VeiculoEntity> novosVeiculos = request.getVeiculos()
                    .stream()
                    .map(veiculoAssembler::fromRequestToEntity)
                    .peek(veiculo -> veiculo.setProprietario(entity))
                    .collect(toList());

            entity.getVeiculos().addAll(novosVeiculos);
        }

        return entity;
    }
}
