package com.solidos.caia.api.invitatios.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.solidos.caia.api.invitatios.entities.InvitationComposeId;
import com.solidos.caia.api.invitatios.entities.InvitationEntity;

public interface InvitationsRepository extends JpaRepository<InvitationEntity, InvitationComposeId> {
  @Query("SELECT i FROM InvitationEntity i WHERE i.token = ?1 AND i.auditMetadata.deletedAt IS NULL")
  Optional<InvitationEntity> findByToken(String token);
}
