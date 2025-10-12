package com.impacta.oficina.domain.enums;

import com.impacta.oficina.domain.response.TipoRevisaoResponse;
import com.impacta.oficina.exceptions.OficinaException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

import static java.math.BigDecimal.ZERO;
import static java.util.Arrays.stream;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Getter
@AllArgsConstructor
public enum TipoRevisaoEnum {
    TROCA_OLEO(1, "Troca de Óleo", 120.00),
    ALINHAMENTO(2, "Alinhamento e Balanceamento", 150.00),
    TROCA_PNEU(3, "Troca de Pneu", 250.00),
    REVISAO_COMPLETA(4, "Revisão Completa (óleo, filtros, velas)", 400.00),
    TROCA_FILTRO_AR(5, "Troca de Filtro de Ar", 50.00),
    TROCA_FILTRO_OLEO(6, "Troca de Filtro de Óleo", 60.00),
    TROCA_BATERIA(7, "Troca de Bateria", 350.00),
    TROCA_VELA(8, "Troca de Velas de Ignição", 80.00),
    LIMPADOR_PARABRISA(9, "Troca de Limpador de Parabrisa", 30.00),
    INSPECAO_TECNICA(10, "Inspeção Técnica", 100.00);

    private final Integer id;
    private final String descricao;
    private final Double valor;

    public static TipoRevisaoResponse getResponse(Integer cdTipoRevisao) {
        TipoRevisaoEnum tipoRevisao = findTipoRevisao(cdTipoRevisao);
        return TipoRevisaoResponse.builder()
                .codigo(tipoRevisao.getId())
                .descricao(tipoRevisao.getDescricao())
                .valor(tipoRevisao.getValor())
                .build();
    }

    public static BigDecimal calcularValorTotal(List<Integer> cdsTipoRevisao) {
        return cdsTipoRevisao
                .stream()
                .map(TipoRevisaoEnum::findTipoRevisao)
                .map(tipo -> BigDecimal.valueOf(tipo.getValor()))
                .reduce(ZERO, BigDecimal::add);
    }

    private static TipoRevisaoEnum findTipoRevisao(Integer cdTipoRevisao) {
        return stream(TipoRevisaoEnum.values())
                .filter(tpRevisao -> tpRevisao.getId().equals(cdTipoRevisao))
                .findFirst()
                .orElseThrow(() -> OficinaException.builder()
                        .httpStatusCode(BAD_REQUEST)
                        .message("Tipo de revisão não encontrado")
                        .description("Tipo de revisão não encontrado para o código: " + cdTipoRevisao)
                        .build());
    }
}
