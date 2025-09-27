package com.impacta.oficina.domain.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VeiculoRequest {

    private Long id;

    @NotNull(message = "O Campo veículo não pode ser nulo")
    @NotEmpty(message = "O Campo veículo não pode ser vazio")
    private String veiculo;

    @NotNull(message = "O Campo marca não pode ser nulo")
    @NotEmpty(message = "O Campo marca não pode ser vazio")
    private String marca;

    @NotNull(message = "O Campo ano não pode ser nulo")
    @Min(value = 1, message = "O Campo ano não pode ser menor que 1")
    private Integer ano;

    private Long proprietario;
}