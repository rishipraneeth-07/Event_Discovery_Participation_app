package com.college.eventapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class InterestsUpdateRequestDTO {
    private List<@NotBlank(message = "Interest cannot be blank")
            @Size(max = 100, message = "Interest must be at most 100 characters") String> interests;
}
