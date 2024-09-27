package com.solidos.caia.api.papers.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.solidos.caia.api.common.entities.AuditMetadata;
import com.solidos.caia.api.users.entities.UserEntity;

import jakarta.persistence.Embedded;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "paper_reviewers")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaperReviewerEntity {
  @EmbeddedId
  private PaperReviewerComposeId paperReviewerComposeId;

  @ManyToOne
  @JoinColumn(name = "paper_id", insertable = false, updatable = false)
  @JsonBackReference
  private PaperEntity paperEntity;

  @ManyToOne
  @JoinColumn(name = "user_id", insertable = false, updatable = false)
  @JsonBackReference
  private UserEntity userEntity;

  @Embedded
  private AuditMetadata auditMetadata;
}
