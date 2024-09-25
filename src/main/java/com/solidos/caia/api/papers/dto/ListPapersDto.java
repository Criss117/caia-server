package com.solidos.caia.api.papers.dto;

import java.util.List;

import com.solidos.caia.api.common.enums.RoleEnum;
import com.solidos.caia.api.papers.entities.PaperEntity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ListPapersDto {
  private List<PaperEntity> papers;
  private List<RoleEnum> withRole;
}
