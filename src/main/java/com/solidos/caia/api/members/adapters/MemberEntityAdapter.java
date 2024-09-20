package com.solidos.caia.api.members.adapters;

import java.util.List;

import com.solidos.caia.api.members.dto.MemberSummary;
import com.solidos.caia.api.members.entities.MemberEntity;

public class MemberEntityAdapter {
  public static MemberSummary toMemberSummary(MemberEntity memberEntity) {
    return MemberSummary.builder()
        .userId(memberEntity.getMemberComposeId().getUserId())
        .role(memberEntity.getRoleEntity().getRole())
        .firstName(memberEntity.getUserEntity().getFirstName())
        .lastName(memberEntity.getUserEntity().getLastName())
        .email(memberEntity.getUserEntity().getEmail())
        .affiliation(memberEntity.getUserEntity().getAffiliation())
        .build();
  }

  public static List<MemberSummary> toMemberSummary(List<MemberEntity> memberEntities) {
    return memberEntities.stream().map(MemberEntityAdapter::toMemberSummary).toList();
  }
}
