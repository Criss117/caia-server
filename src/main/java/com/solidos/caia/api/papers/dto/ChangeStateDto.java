package com.solidos.caia.api.papers.dto;

import com.solidos.caia.api.papers.enums.PaperStateEnum;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
@Data
public class ChangeStateDto {
    @NotNull(message = "State connot be null")
    private PaperStateEnum state;
}
