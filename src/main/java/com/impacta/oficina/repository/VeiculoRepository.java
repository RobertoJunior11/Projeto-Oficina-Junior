package com.impacta.oficina.repository;

import com.impacta.oficina.domain.entity.VeiculoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VeiculoRepository extends JpaRepository<VeiculoEntity, Long> {

    Page<VeiculoEntity> findAll(Specification<VeiculoEntity> specification, Pageable pageable);
}
