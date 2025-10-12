package com.impacta.oficina.domain.request;

import jakarta.validation.constraints.Future;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
public class RevisaoPatchRequest {

    private Long id;

    @Future(message = "A data de revisão não pode ser no passado")
    private LocalDateTime dtRevisao;

    private String descricao;
    private BigDecimal vlServico;
    private List<Integer> tiposRevisao;

    @Builder.Default
    private Boolean concluida = false;
}
