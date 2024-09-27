package com.solidos.caia.api.papers.entities;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.solidos.caia.api.common.entities.AuditMetadata;
import com.solidos.caia.api.conferences.entities.ConferenceEntity;
import com.solidos.caia.api.papers.enums.PaperStateEnum;
import com.solidos.caia.api.users.entities.UserEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "papers")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class PaperEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "owner_id", updatable = false)
  @JsonBackReference
  private UserEntity userEntity;

  @ManyToOne
  @JoinColumn(name = "conference_id", updatable = false)
  @JsonBackReference
  private ConferenceEntity conferenceEntity;

  @OneToMany(targetEntity = PaperReviewerEntity.class, mappedBy = "paperEntity", fetch = FetchType.LAZY)
  @JsonManagedReference
  private List<PaperReviewerEntity> paperReviewers;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  private String description;

  @Column(nullable = false)
  private String fileName;

  // TODO: cambiar a List<String>
  @Column(nullable = false)
  private String keys;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  @Builder.Default
  private PaperStateEnum state = PaperStateEnum.WAITING_FOR_REVIEW;

  @Embedded
  private AuditMetadata auditMetadata;
}
