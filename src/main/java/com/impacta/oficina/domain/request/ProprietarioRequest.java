package com.impacta.oficina.domain.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
public class ProprietarioRequest {

    private Long id;

    @NotNull(message = "O Campo nome n�o pode ser nulo")
    @NotEmpty(message = "O Campo nome n�o pode ser vazio")
    private String nome;

    @NotNull(message = "O Campo email n�o pode ser nulo")
    @NotEmpty(message = "O Campo email n�o pode ser vazio")
    private String email;

    @NotNull(message = "O Campo telefone n�o pode ser nulo")
    @NotEmpty(message = "O Campo telefone n�o pode ser vazio")
    private String telefone;

    @Builder.Default
    private List<VeiculoRequest> veiculos = new ArrayList<>();
}
