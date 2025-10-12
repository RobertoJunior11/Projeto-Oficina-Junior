package com.impacta.oficina.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.impacta.oficina.domain.enums.TipoRevisaoEnum;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;
import static jakarta.persistence.GenerationType.SEQUENCE;

@Data
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@Table(name = "TB_REVISAO")
@EqualsAndHashCode(callSuper = true)
public class RevisaoEntity extends GenericEntity {

    @Id
    @GeneratedValue(strategy = SEQUENCE)
    @SequenceGenerator(name = "revisao_seq", sequenceName = "SQ_REVISAO", allocationSize = 1)
    private Long id;

    @Column(name = "DT_REVISAO")
    @JsonFormat(shape = STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT-03")
    private LocalDateTime dtRevisao;

    @Column(name = "DS_PROBLEMA", length = 500)
    private String descricao;

    @Column(name = "VL_SERVICO", precision = 10, scale = 2)
    private BigDecimal vlServico;

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "TB_REVISAO_TIPOS", joinColumns = @JoinColumn(name = "ID_REVISAO"))
    @Enumerated(EnumType.STRING)
    @Column(name = "TP_REVISAO")
    private List<TipoRevisaoEnum> tiposRevisao = new ArrayList<>();

    @Builder.Default
    @Column(name = "FL_CONCLUIDA", nullable = false)
    private Boolean concluida = false;

    @ManyToOne
    @JoinColumn(name = "ID_VEICULO", nullable = false)
    private VeiculoEntity veiculo;
}
