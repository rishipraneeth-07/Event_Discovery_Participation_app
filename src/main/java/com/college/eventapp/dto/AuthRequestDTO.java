package com.college.eventapp.dto;

import com.college.eventapp.model.Role;
import lombok.Data;

@Data
public class AuthRequestDTO {
    private String name;     // used in register
    private String email;
    private String password;
    private Role role;
}
