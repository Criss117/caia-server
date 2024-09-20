package com.solidos.caia.api.invitatios.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateInvitationDto {
  @NotNull(message = "User cannot be null")
  private Long userId;

  @NotNull(message = "Conference cannot be null")
  private Long conferenceId;

  @NotNull(message = "Message cannot be null")
  @Size(max = 225, message = "Message must be less than 225 characters")
  private String message;
}
