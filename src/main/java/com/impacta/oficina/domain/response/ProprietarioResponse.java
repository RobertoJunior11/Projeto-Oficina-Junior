package com.impacta.oficina.domain.response;

import com.impacta.oficina.domain.entity.VeiculoEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
public class ProprietarioResponse {

    private Long id;
    private String nome;
    private String email;
    private String telefone;

    @Builder.Default
    private List<VeiculoEntity> veiculos = new ArrayList<>();
}
