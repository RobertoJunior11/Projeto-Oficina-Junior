package com.impacta.oficina.domain.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
public class RevisaoRequest {

    private Long id;

    private LocalDateTime dtRevisao;

    @NotNull(message = "A descricao do problema não pode ser nula")
    @NotEmpty(message = "A descricao do problema não pode ser vazia")
    private String descricao;

    private BigDecimal vlServico;

    @Builder.Default
    private Boolean concluida = false;

    @Builder.Default
    private List<Integer> tiposRevisao = new ArrayList<>();

    @NotNull(message = "O veículo não pode ser nulo")
    private VeiculoRequest veiculo;
}
