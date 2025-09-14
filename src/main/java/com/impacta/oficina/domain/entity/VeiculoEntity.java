package com.impacta.oficina.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import java.time.LocalDateTime;

import static jakarta.persistence.GenerationType.SEQUENCE;
import static java.time.LocalDateTime.now;

@Data
@Entity
@FieldNameConstants
@Table(name = "TB_VEICULO")
public class VeiculoEntity {

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

    @Column(name = "DT_CRIACAO", updatable = false)
    private LocalDateTime dtCriacao;

    @Column(name = "DT_ATUALIZACAO")
    private LocalDateTime dtAtualizacao;

    /**
     * Define automaticamente os timestamps de criação e atualização
     * quando uma nova entidade é persistida no banco de dados.
     */
    @PrePersist
    protected void onCreate() {
        dtCriacao = now();
        dtAtualizacao = now();
    }

    /**
     * Atualiza automaticamente o timestamp de atualização
     * sempre que a entidade for modificada no banco de dados.
     */
    @PreUpdate
    protected void onUpdate() {
        dtAtualizacao = now();
    }
}
