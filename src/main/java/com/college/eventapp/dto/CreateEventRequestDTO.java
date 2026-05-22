package com.college.eventapp.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateEventRequestDTO {
    @NotBlank(message = "Title is required")
    @Size(max = 150, message = "Title must be at most 150 characters")
    private String title;

    @NotBlank(message = "Description is required")
    @Size(max = 3000, message = "Description must be at most 3000 characters")
    private String description;

    @NotBlank(message = "Location is required")
    @Size(max = 255, message = "Location must be at most 255 characters")
    private String location;

    @NotBlank(message = "Date is required")
    private String date;

    private String time;

    @NotBlank(message = "Category is required")
    @Size(max = 100, message = "Category must be at most 100 characters")
    private String category;

    @NotNull(message = "Capacity is required")
    @Min(value = 0, message = "Capacity must be 0 (unlimited) or greater")
    @Max(value = 100000, message = "Capacity is too large")
    private Integer capacity;

    @NotNull(message = "Organizer id is required")
    @Positive(message = "Organizer id must be positive")
    private Long organizerId;

}
