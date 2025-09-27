package com.impacta.oficina.repository;

import com.impacta.oficina.domain.entity.ProprietarioEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProprietarioRepository extends JpaRepository<ProprietarioEntity, Long> {

    Page<ProprietarioEntity> findAll(Specification<ProprietarioEntity> specification, Pageable pageable);
}
