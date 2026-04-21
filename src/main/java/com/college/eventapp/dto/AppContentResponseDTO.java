package com.college.eventapp.dto;

import lombok.Data;

import java.util.Map;

@Data
public class AppContentResponseDTO {
    private String about;
    private String termsSummary;
    private Map<String, String> helpCenter;
}
