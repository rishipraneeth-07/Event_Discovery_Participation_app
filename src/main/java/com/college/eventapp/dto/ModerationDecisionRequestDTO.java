package com.college.eventapp.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ModerationDecisionRequestDTO {
    @JsonAlias("reason")
    @Size(max = 1000, message = "Moderation note must be at most 1000 characters")
    private String note;
}
