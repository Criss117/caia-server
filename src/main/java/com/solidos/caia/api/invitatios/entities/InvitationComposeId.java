package com.solidos.caia.api.invitatios.entities;

import java.io.Serializable;
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
public class InvitationComposeId implements Serializable {
  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "conference_id", nullable = false)
  private Long conferenceId;

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    InvitationComposeId that = (InvitationComposeId) o;
    return Objects.equals(userId, that.userId) &&
        Objects.equals(conferenceId, that.conferenceId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userId, conferenceId);
  }
}