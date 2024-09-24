package com.solidos.caia.api.papers.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.solidos.caia.api.papers.entities.PaperEntity;

@Repository
public interface PaperRepository extends JpaRepository<PaperEntity, Long> {

}
