package com.solidos.caia.api.members.dto;

import com.solidos.caia.api.common.enums.RoleEnum;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MemberSummary {
  private Long userId;
  private RoleEnum role;
  private String firstName;
  private String lastName;
  private String email;
  private String affiliation;
}
