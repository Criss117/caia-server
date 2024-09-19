package com.solidos.caia.api.members;

import java.util.List;

import org.apache.logging.log4j.util.InternalException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.solidos.caia.api.common.enums.RoleEnum;
import com.solidos.caia.api.members.dto.CreateMemberDto;
import com.solidos.caia.api.members.entities.MemberComposeId;
import com.solidos.caia.api.members.entities.MemberEntity;
import com.solidos.caia.api.members.repositories.MemberRepository;
import com.solidos.caia.api.members.repositories.RoleRepository;

@Service
public class MemberService {
  private MemberRepository memberRepository;
  private RoleRepository roleRepository;

  public MemberService(MemberRepository memberRepository, RoleRepository roleRepository) {
    this.memberRepository = memberRepository;
    this.roleRepository = roleRepository;
  }

  @Transactional
  public void createMember(CreateMemberDto createMemberDto) {
    Long roleId = roleRepository.findRoleId(createMemberDto.getRole());

    MemberComposeId memberComposeId = MemberComposeId.builder()
        .roleId(roleId)
        .userId(createMemberDto.getUserId())
        .conferenceId(createMemberDto.getConferenceId())
        .build();

    MemberEntity memberEntity = MemberEntity.builder()
        .memberComposeId(memberComposeId)
        .build();

    try {

      memberRepository.save(memberEntity);
    } catch (Exception e) {
      throw new InternalException("Error creating member");
    }
  }

  public List<MemberEntity> findByRole(Long userId, RoleEnum role, Pageable pageable) {
    return memberRepository.findAllByUserId(userId, pageable)
        .stream()
        .filter(m -> m.getRoleEntity().getRole() == role)
        .toList();
  }

}
