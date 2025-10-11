package com.impacta.oficina.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;
import static java.time.LocalDateTime.now;

@Data
@SuperBuilder
@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
public class GenericEntity {

    @Column(name = "DT_CRIACAO", nullable = false, updatable = false)
    @JsonFormat(shape = STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT-03")
    private LocalDateTime dtCriacao;

    @Column(name = "DT_ATUALIZACAO", nullable = false)
    @JsonFormat(shape = STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT-03")
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
