package com.solidos.caia.api.papers.dto;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreatePaperDto {

  @NotNull(message = "Title cannot be null")
  private String title;

  @NotNull(message = "Description cannot be null")
  private String description;

  @NotNull(message = "ConferenceId cannot be null")
  private Long conferenceId;

  @NotNull(message = "Keys cannot be null")
  private List<String> keys;
}
