package com.impacta.oficina.assembler;

import com.impacta.oficina.domain.entity.ProprietarioEntity;
import com.impacta.oficina.domain.entity.VeiculoEntity;
import com.impacta.oficina.domain.request.VeiculoRequest;
import com.impacta.oficina.domain.response.VeiculoResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VeiculoAssembler {

    private final ModelMapper modelMapper;

    /**
     * Converte um VeiculoRequest em VeiculoEntity.
     *
     * @param request dados de entrada do carro
     * @return entidade do carro
     */
    public VeiculoEntity fromRequestToEntity(VeiculoRequest request) {
        VeiculoEntity entity = modelMapper.map(request, VeiculoEntity.class);
        
        // Mapear proprietário se fornecido
        if (request.getProprietario() != null) {
            ProprietarioEntity proprietario = ProprietarioEntity.builder()
                    .id(request.getProprietario())
                    .build();
            entity.setProprietario(proprietario);
        }
        
        return entity;
    }

    /**
     * Converte um VeiculoEntity em VeiculoResponse.
     *
     * @param entity entidade do carro
     * @return resposta com os dados do carro
     */
    public VeiculoResponse fromEntityToResponse(VeiculoEntity entity) {
        return modelMapper.map(entity, VeiculoResponse.class);
    }

    /**
     * Atualiza uma entidade existente com dados de um request.
     * Apenas campos não nulos são atualizados.
     *
     * @param request dados parciais do carro
     * @param entity  entidade a ser atualizada
     * @return entidade atualizada
     */
    public VeiculoEntity fromDTO(VeiculoRequest request, VeiculoEntity entity) {
        if (request.getVeiculo() != null) {
            entity.setVeiculo(request.getVeiculo());
        }

        if (request.getMarca() != null) {
            entity.setMarca(request.getMarca());
        }

        if (request.getAno() != null) {
            entity.setAno(request.getAno());
        }

        return entity;
    }
}
