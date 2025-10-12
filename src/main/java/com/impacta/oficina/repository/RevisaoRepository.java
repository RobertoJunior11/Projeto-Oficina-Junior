package com.impacta.oficina.repository;

import com.impacta.oficina.domain.entity.RevisaoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RevisaoRepository extends JpaRepository<RevisaoEntity, Long> {

    Page<RevisaoEntity> findAll(Specification<RevisaoEntity> specification, Pageable pageable);
}
