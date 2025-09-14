package com.impacta.oficina.domain.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VeiculoResponse {
    private Long id;
    private String veiculo;
    private String marca;
    private Integer ano;
    private LocalDateTime dtCriacao;
    private LocalDateTime dtAtualizacao;
}
