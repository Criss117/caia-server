package com.solidos.caia.api.invitatios;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.solidos.caia.api.common.enums.RoleEnum;
import com.solidos.caia.api.common.utils.TokenGenerator;
import com.solidos.caia.api.invitatios.dto.CreateInvitationDto;
import com.solidos.caia.api.invitatios.dto.InvitationResponseDto;
import com.solidos.caia.api.invitatios.entities.InvitationComposeId;
import com.solidos.caia.api.invitatios.entities.InvitationEntity;
import com.solidos.caia.api.invitatios.models.InvitationStatusEnum;
import com.solidos.caia.api.invitatios.repositories.InvitationsRepository;
import com.solidos.caia.api.members.MemberService;
import com.solidos.caia.api.members.MembersPermissions;
import com.solidos.caia.api.members.dto.CreateMemberDto;

@Service
public class InvitationsService {
  private InvitationsRepository invitationsRepository;
  private MemberService memberService;
  private MembersPermissions membersPermissions;

  public InvitationsService(
      MemberService memberService,
      MembersPermissions membersPermissions,
      InvitationsRepository invitationsRepository) {
    this.invitationsRepository = invitationsRepository;
    this.memberService = memberService;
    this.membersPermissions = membersPermissions;
  }

  @Transactional
  public InvitationEntity createInvitation(CreateInvitationDto createInvitationDto) {
    membersPermissions.hasConferencePermission(createInvitationDto.getConferenceId(), RoleEnum.ORGANIZER);

    Boolean existsMember = memberService
        .findByComposeId(createInvitationDto.getConferenceId(), createInvitationDto.getUserId()).isPresent();

    if (existsMember) {
      throw new IllegalStateException("Member already exists");
    }

    InvitationComposeId invitationId = InvitationComposeId.builder()
        .userId(createInvitationDto.getUserId())
        .conferenceId(createInvitationDto.getConferenceId())
        .build();

    if (invitationsRepository.existsById(invitationId)) {
      throw new IllegalStateException("Invitation already exists");
    }

    InvitationEntity invitationEntity = InvitationEntity.builder()
        .invitationComposeId(invitationId)
        .token(TokenGenerator.generate())
        .message(createInvitationDto.getMessage())
        .build();

    InvitationEntity newInvitation = invitationsRepository.save(invitationEntity);

    // TODO: Send email

    return newInvitation;
  }

  @Transactional
  public void invitationResponse(String token, InvitationResponseDto invitationResponseDto) {
    InvitationEntity invitation = invitationsRepository.findByToken(token)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invitation not found"));

    if (invitation.getStatus() != InvitationStatusEnum.PENDING) {
      throw new IllegalArgumentException("Invitation already responded");
    }

    invitation.setStatus(invitationResponseDto.getStatus());
    invitation.setRespondedAt(LocalDateTime.now());
    invitation.setToken(null);

    invitationsRepository.save(invitation);

    if (invitation.getStatus() == InvitationStatusEnum.ACCEPTED) {
      memberService.createMember(
          CreateMemberDto.builder()
              .role(RoleEnum.REVIEWER)
              .userId(invitation.getInvitationComposeId().getUserId())
              .conferenceId(invitation.getInvitationComposeId().getConferenceId())
              .build());
    }

    // TODO: Send email
  }

  @Transactional
  public void delete(String token) {
    InvitationEntity currentInvitation = invitationsRepository.findByToken(token).orElseThrow(
        () -> new IllegalStateException("Invitation not found"));

    currentInvitation.getAuditMetadata().setDeletedAt(LocalDateTime.now());
    invitationsRepository.save(currentInvitation);
  }

  @Transactional
  public void delete(InvitationComposeId invitationComposeId) {
    InvitationEntity currentInvitation = invitationsRepository.findById(invitationComposeId).orElseThrow(
        () -> new IllegalStateException("Invitation not found"));

    currentInvitation.getAuditMetadata().setDeletedAt(LocalDateTime.now());
    invitationsRepository.save(currentInvitation);
  }

  @Transactional
  public void delete(InvitationEntity invitation) {
    invitation.getAuditMetadata().setDeletedAt(LocalDateTime.now());
    invitationsRepository.save(invitation);
  }
}
