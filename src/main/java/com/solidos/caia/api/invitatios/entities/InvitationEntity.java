package com.solidos.caia.api.invitatios.entities;

import java.time.LocalDateTime;

import com.solidos.caia.api.common.entities.AuditMetadata;
import com.solidos.caia.api.conferences.entities.ConferenceEntity;
import com.solidos.caia.api.invitatios.models.InvitationStatusEnum;
import com.solidos.caia.api.users.entities.UserEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "reviewer_invitations")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InvitationEntity {
  @EmbeddedId
  private InvitationComposeId invitationComposeId;

  @ManyToOne
  @JoinColumn(name = "conference_id", insertable = false, updatable = false)
  private ConferenceEntity conferenceEntity;

  @ManyToOne
  @JoinColumn(name = "user_id", insertable = false, updatable = false)
  private UserEntity userEntity;

  private String token;

  @Column(name = "status", nullable = false)
  @Enumerated(EnumType.STRING)
  @Builder.Default
  private InvitationStatusEnum status = InvitationStatusEnum.PENDING;

  @Column(name = "responded_at")
  private LocalDateTime respondedAt;

  @Column(name = "message")
  private String message;

  @Embedded
  private AuditMetadata auditMetadata;
}
