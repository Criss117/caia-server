package com.solidos.caia.api.papers.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.solidos.caia.api.papers.entities.PaperEntity;

@Repository
public interface PaperRepository extends JpaRepository<PaperEntity, Long> {
  @Query("SELECT p FROM PaperEntity p " +
      "WHERE p.conferenceEntity.id = ?1 " +
      "AND p.auditMetadata.deletedAt IS NULL")
  List<PaperEntity> findAllByConferenceId(Long conferenceId);

  @Override
  @Query("SELECT p FROM PaperEntity p " +
      "WHERE p.id = ?1 " +
      "AND p.auditMetadata.deletedAt IS NULL")
  @SuppressWarnings("null")
  Optional<PaperEntity> findById(Long id);
}
