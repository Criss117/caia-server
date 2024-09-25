package com.solidos.caia.api.members;

import java.util.Optional;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.solidos.caia.api.auth.AuthService;
import com.solidos.caia.api.common.enums.RoleEnum;
import com.solidos.caia.api.members.entities.MemberComposeId;
import com.solidos.caia.api.members.entities.MemberEntity;
import com.solidos.caia.api.members.entities.RoleEntity;
import com.solidos.caia.api.members.repositories.MemberRepository;
import com.solidos.caia.api.members.repositories.RoleRepository;

@Service
public class MembersPermissions {
  private MemberRepository memberRepository;
  private AuthService authService;
  private RoleRepository roleRepository;

  public MembersPermissions(MemberRepository memberRepository, AuthService authService, RoleRepository roleRepository) {
    this.memberRepository = memberRepository;
    this.authService = authService;
    this.roleRepository = roleRepository;
  }

  public Long hasConferencePermission(Long conferenceId, RoleEnum role) {
    Long userId = authService.getUserIdByEmail();

    if (userId == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
    }

    Long roleId = roleRepository.findRoleId(role);

    if (roleId == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid role");
    }

    Optional<MemberEntity> member = memberRepository.findById(
        MemberComposeId.builder()
            .userId(userId)
            .conferenceId(conferenceId)
            .roleId(roleId)
            .build());

    if (!member.isPresent()) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not authorized");
    }

    if (member.get().getRoleEntity().getRole() != role) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not authorized");
    }

    return userId;
  }

  public List<RoleEnum> getUserRole(Long conferenceId) {
    Long userId = authService.getUserIdByEmail();

    if (userId == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
    }

    List<MemberEntity> members = memberRepository.findByComposeId(conferenceId, userId);

    if (members.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not authorized");
    }

    return members.stream()
        .map(MemberEntity::getRoleEntity)
        .map(RoleEntity::getRole)
        .toList();
  }
}
