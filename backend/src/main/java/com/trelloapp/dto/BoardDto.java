package com.trelloapp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardDto {

    private Long id;

    @NotBlank(message = "Board name is required")
    private String name;

    private String description;

    private List<TaskDto> tasks;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}