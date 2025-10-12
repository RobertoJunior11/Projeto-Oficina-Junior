package com.impacta.oficina.domain.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TipoRevisaoResponse {

    private Integer codigo;
    private String descricao;
    private Double valor;
}
