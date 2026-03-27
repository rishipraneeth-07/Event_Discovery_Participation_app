package com.college.eventapp.dto;

import com.college.eventapp.model.Role;
import lombok.Data;

@Data
public class AuthResponseDTO {
    private String token;
    private Long userId;
    private String name;
    private String email;
    private Role role;
}
