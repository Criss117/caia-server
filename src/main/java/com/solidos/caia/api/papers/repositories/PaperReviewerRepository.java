package com.solidos.caia.api.papers.repositories;

import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.solidos.caia.api.papers.entities.PaperReviewerComposeId;
import com.solidos.caia.api.papers.entities.PaperReviewerEntity;

@Repository
public interface PaperReviewerRepository extends JpaRepository<PaperReviewerEntity, PaperReviewerComposeId> {

  @Query("SELECT pr FROM PaperReviewerEntity pr WHERE " +
      "pr.paperReviewerComposeId.userId = ?1 AND " +
      "pr.paperReviewerComposeId.paperId = ?2 AND " +
      "pr.auditMetadata.deletedAt is NULL")
  Optional<PaperReviewerEntity> findByComposeId(Long userId, Long paperId);

  @Query("SELECT pr FROM PaperReviewerEntity pr WHERE " +
      "pr.paperReviewerComposeId.userId = ?1 AND " +
      "pr.auditMetadata.deletedAt is NULL")
  List<PaperReviewerEntity> findAllByUserId(Long userId);
}