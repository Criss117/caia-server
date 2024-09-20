package com.solidos.caia.api.invitatios;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.solidos.caia.api.common.models.CommonResponse;
import com.solidos.caia.api.invitatios.dto.CreateInvitationDto;
import com.solidos.caia.api.invitatios.dto.InvitationResponseDto;
import com.solidos.caia.api.invitatios.entities.InvitationEntity;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/invitations")
@PreAuthorize("authenticated")
public class InvitationsController {

  private InvitationsService invitationsService;

  public InvitationsController(InvitationsService invitationsService) {
    this.invitationsService = invitationsService;
  }

  @PostMapping
  public ResponseEntity<CommonResponse<InvitationEntity>> sendInvitation(
      @RequestBody @Valid CreateInvitationDto entity) {
    InvitationEntity newInvitation = invitationsService.createInvitation(entity);

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(CommonResponse.success(newInvitation, "Invitation sent successfully"));
  }

  @PostMapping("/{token}")
  public ResponseEntity<CommonResponse<?>> acceptInvitation(
      @PathVariable @Valid String token,
      @RequestBody @Valid InvitationResponseDto invResDto) {

    invitationsService.invitationResponse(token, invResDto);

    return ResponseEntity.ok(CommonResponse.success("Response received successfully"));
  }
}
