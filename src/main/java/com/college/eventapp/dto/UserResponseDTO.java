package com.college.eventapp.dto;

import com.college.eventapp.model.Role;
import lombok.Data;

@Data
public class UserResponseDTO {
    private Long id;
    private String name;
    private String email;
    private Role role;
}
