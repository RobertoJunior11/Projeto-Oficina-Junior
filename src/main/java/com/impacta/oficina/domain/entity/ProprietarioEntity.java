package com.impacta.oficina.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.SEQUENCE;

@Data
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@Table(name = "TB_PROPRIETARIO")
@EqualsAndHashCode(callSuper = true)
public class ProprietarioEntity extends GenericEntity {

    @Id
    @GeneratedValue(strategy = SEQUENCE)
    @SequenceGenerator(name = "proprietario_seq", sequenceName = "SQ_PROPRIETARIO", allocationSize = 1)
    private Long id;

    @Column(name = "NM_PROPRIETARIO", nullable = false)
    private String nome;

    @Column(name = "DS_EMAIL", nullable = false)
    private String email;

    @Column(name = "NR_TELEFONE", nullable = false)
    private String telefone;

    @Builder.Default
    @OneToMany(mappedBy = "proprietario", cascade = ALL, fetch = LAZY)
    private List<VeiculoEntity> veiculos = new ArrayList<>();
}
