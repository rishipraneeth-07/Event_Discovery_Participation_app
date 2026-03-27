package com.college.eventapp.dto;

import lombok.Data;

@Data
public class CreateEventRequestDTO {
    private String title;
    private String description;
    private String location;
    private String date;
    private String time;
    private String category;
    private Integer capacity;
    private Long organizerId;

}
