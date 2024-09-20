package com.solidos.caia.api.invitatios.dto;

import com.solidos.caia.api.invitatios.models.InvitationStatusEnum;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InvitationResponseDto {
  @NotNull(message = "status cannot be null")
  private InvitationStatusEnum status;
}
