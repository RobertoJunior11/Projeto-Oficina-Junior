package com.impacta.oficina.domain.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
public class RevisaoResponse {

    private Long id;
    private LocalDateTime dtRevisao;
    private String descricao;
    private BigDecimal vlServico;

    @Builder.Default
    private List<TipoRevisaoResponse> tiposRevisao = new ArrayList<>();

    private Boolean concluida;
    private VeiculoResponse veiculo;
}
