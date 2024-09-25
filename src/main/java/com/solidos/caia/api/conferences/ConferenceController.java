package com.solidos.caia.api.conferences;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.solidos.caia.api.auth.AuthService;
import com.solidos.caia.api.common.enums.RoleEnum;
import com.solidos.caia.api.common.models.CommonResponse;
import com.solidos.caia.api.conferences.dto.ConferenceSummaryDto;
import com.solidos.caia.api.conferences.dto.ConferencesByRoleDto;
import com.solidos.caia.api.conferences.dto.CreateConferenceDto;
import com.solidos.caia.api.conferences.entities.ConferenceEntity;
import com.solidos.caia.api.members.dto.MemberSummary;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/conferences")
@PreAuthorize("permitAll()")
public class ConferenceController {

  private ConferenceService conferenceService;
  private AuthService authService;

  public ConferenceController(
      ConferenceService conferenceService,
      AuthService authService) {
    this.conferenceService = conferenceService;
    this.authService = authService;
  }

  @GetMapping
  @PreAuthorize("permitAll()")
  public ResponseEntity<CommonResponse<List<ConferenceSummaryDto>>> findAllConferences(
      @RequestParam @Nullable String query,
      @RequestParam @Nullable Integer page,
      @RequestParam @Nullable Integer offSet) {
    var conferences = conferenceService.findAllConferences(query, page, offSet);

    return ResponseEntity.ok(CommonResponse.success(conferences, "Conferences found"));
  }

  @PostMapping
  @PreAuthorize("authenticated")
  public ResponseEntity<CommonResponse<ConferenceEntity>> postMethodName(
      @RequestBody CreateConferenceDto createConferenceDto) {

    ConferenceEntity newConference = conferenceService.createConference(createConferenceDto);

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(CommonResponse.success(newConference, "Conference created successfully"));
  }

  @GetMapping("/{idOrSlug}")
  @PreAuthorize("permitAll()")
  public ResponseEntity<CommonResponse<ConferenceEntity>> getConference(@PathVariable String idOrSlug) {
    var conference = conferenceService.findByIdOrSlug(idOrSlug);

    return ResponseEntity.ok(CommonResponse.success(conference, "Conference found"));
  }

  @GetMapping("/by-role/{role}")
  public ResponseEntity<CommonResponse<List<ConferenceSummaryDto>>> findConferencesByRole(
      @PathVariable @Valid RoleEnum role,
      @RequestParam @Nullable String query,
      @RequestParam @Nullable Integer page,
      @RequestParam @Nullable Integer offSet) {
    Long userId = authService.getUserIdByEmail();

    ConferencesByRoleDto conferencesByRoleDto = ConferencesByRoleDto
        .builder()
        .query(query)
        .page(page)
        .offSet(offSet)
        .role(role)
        .userId(userId)
        .build();

    var conferencesByRole = conferenceService.findConferencesByRole(conferencesByRoleDto);

    return ResponseEntity.ok(CommonResponse.success(conferencesByRole, "Conferences found"));
  }

  @GetMapping("/{idOrSlug}/members")
  @PreAuthorize("permitAll()")
  public ResponseEntity<CommonResponse<List<MemberSummary>>> findMembers(
      @PathVariable String idOrSlug,
      @RequestParam @Nullable Integer page,
      @RequestParam @Nullable Integer offSet) {

    List<MemberSummary> members = conferenceService.findMembers(idOrSlug, page, offSet);

    return ResponseEntity.ok(CommonResponse.success(members, "Members found"));
  }

}
