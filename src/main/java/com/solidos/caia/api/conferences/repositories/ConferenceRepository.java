package com.solidos.caia.api.conferences.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.solidos.caia.api.conferences.entities.ConferenceEntity;

@Repository
public interface ConferenceRepository extends JpaRepository<ConferenceEntity, Long> {

  @Query("SELECT c FROM ConferenceEntity c " +
      "WHERE (LOWER(c.name) LIKE %?1% " +
      "OR LOWER(c.slug) LIKE %?1%) " +
      "AND c.auditMetadata.deletedAt IS NULL")
  List<ConferenceEntity> findAllConferences(String query, Pageable pageable);

  @Query("SELECT c FROM ConferenceEntity c " +
      "WHERE (c.id = ?1 OR c.slug = ?2) " +
      "AND c.auditMetadata.deletedAt IS NULL")
  Optional<ConferenceEntity> findByIdOrSlug(Long id, String slug);

}
