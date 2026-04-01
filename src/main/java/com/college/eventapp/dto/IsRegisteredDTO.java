package com.college.eventapp.dto;

import lombok.Data;

@Data
public class IsRegisteredDTO {
    private boolean registered;
    private Long registrationId;
}