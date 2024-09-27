package com.solidos.caia.api.conferences;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.solidos.caia.api.auth.AuthService;
import com.solidos.caia.api.common.enums.RoleEnum;
import com.solidos.caia.api.common.utils.PaginationParams;
import com.solidos.caia.api.common.utils.SlugGenerator;
import com.solidos.caia.api.conferences.apdaptes.ConferenceEntityAdapter;
import com.solidos.caia.api.conferences.dto.ConferenceSummaryDto;
import com.solidos.caia.api.conferences.dto.ConferencesByRoleDto;
import com.solidos.caia.api.conferences.dto.CreateConferenceDto;
import com.solidos.caia.api.conferences.entities.ConferenceEntity;
import com.solidos.caia.api.conferences.repositories.ConferenceRepository;
import com.solidos.caia.api.members.MemberService;
import com.solidos.caia.api.members.dto.CreateMemberDto;
import com.solidos.caia.api.members.dto.MemberSummary;
import com.solidos.caia.api.members.entities.MemberEntity;

@Service
public class ConferenceService {
  private ConferenceRepository conferenceRepository;
  private MemberService memberService;
  private AuthService authService;

  public ConferenceService(
      ConferenceRepository conferenceRepository,
      MemberService memberService,
      AuthService authService) {
    this.conferenceRepository = conferenceRepository;
    this.memberService = memberService;
    this.authService = authService;
  }

  @Transactional
  public ConferenceEntity createConference(CreateConferenceDto createConferenceDto) {
    Long userId = authService.getUserIdByEmail();

    ConferenceEntity newConference = conferenceRepository.save(
        ConferenceEntity.builder()
            .name(createConferenceDto.getName())
            .description(createConferenceDto.getDescription())
            .slug(SlugGenerator.generate(createConferenceDto.getName()))
            .build());

    memberService.createMember(
        CreateMemberDto.builder()
            .role(RoleEnum.ORGANIZER)
            .userId(userId)
            .conferenceId(newConference.getId())
            .build());

    return newConference;

  }

  public List<ConferenceSummaryDto> findAllConferences(String query, Integer page, Integer offSet) {
    PaginationParams paginationParams = PaginationParams.withQuery(page, offSet, query);

    if (paginationParams.getQuery() == null || paginationParams.getQuery().length() < 3) {
      return conferenceRepository
          .findAll(paginationParams.getPageable())
          .stream()
          .map(ConferenceEntityAdapter::toConferenceSummary)
          .toList();
    }

    return conferenceRepository
        .findAllConferences(
            paginationParams.getQuery(),
            paginationParams.getPageable())
        .stream()
        .map(ConferenceEntityAdapter::toConferenceSummary)
        .toList();
  }

  public ConferenceEntity findByIdOrSlug(String idOrSlug) {

    Long conferenceId = -1L;
    String slug = idOrSlug;

    try {
      conferenceId = Long.parseLong(idOrSlug);
    } catch (Exception e) {
      slug = idOrSlug;
    }

    return conferenceRepository
        .findByIdOrSlug(conferenceId, slug)
        .orElseThrow(() -> new IllegalArgumentException("Conference not found"));
  }

  public List<ConferenceSummaryDto> findConferencesByRole(ConferencesByRoleDto data) {
    PaginationParams pagParams = PaginationParams.withQuery(data.getPage(), data.getOffSet(), data.getQuery());

    List<MemberEntity> members = memberService.findByRole(
        data.getUserId(),
        data.getRole(),
        pagParams.getPageable());

    if (data.getQuery() == null || data.getQuery().length() < 3) {
      return members.stream()
          .map(m -> ConferenceEntityAdapter.toConferenceSummary(m.getConferenceEntity())).toList();
    }

    return members.stream()
        .filter(m -> m.getConferenceEntity().getName().contains(data.getQuery()))
        .map(m -> ConferenceEntityAdapter.toConferenceSummary(m.getConferenceEntity())).toList();
  }

  public List<MemberSummary> findMembers(String idOrSlug, Integer page, Integer offSet) {
    Long conferenceId = null;
    String slug = idOrSlug;

    try {
      conferenceId = Long.parseLong(idOrSlug);
    } catch (Exception e) {
      slug = idOrSlug;
    }

    if (conferenceId == null) {
      conferenceId = this.findByIdOrSlug(slug).getId();
    }

    Pageable pageable = PaginationParams.of(page, offSet);

    return memberService.findByConferenceId(conferenceId, pageable);
  }
}
