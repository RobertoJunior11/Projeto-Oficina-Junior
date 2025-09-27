package com.impacta.oficina.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

import static jakarta.persistence.GenerationType.SEQUENCE;

@Data
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@Table(name = "TB_VEICULO")
@EqualsAndHashCode(callSuper = true)
public class VeiculoEntity extends GenericEntity {

    @Id
    @GeneratedValue(strategy = SEQUENCE)
    @SequenceGenerator(name = "veiculo_seq", sequenceName = "SQ_VEICULO", allocationSize = 1)
    private Long id;

    @Column(name = "NM_VEICULO", nullable = false)
    private String veiculo;

    @Column(name = "NM_MARCA", nullable = false)
    private String marca;

    @Column(name = "ANO", nullable = false)
    private Integer ano;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "ID_PROPRIETARIO")
    private ProprietarioEntity proprietario;
}
