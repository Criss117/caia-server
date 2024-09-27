package com.solidos.caia.api.papers.entities;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaperReviewerComposeId {
  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "paper_id", nullable = false)
  private Long paperId;

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    PaperReviewerComposeId that = (PaperReviewerComposeId) o;
    return Objects.equals(userId, that.userId) &&
        Objects.equals(paperId, that.paperId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userId, paperId);
  }
}
