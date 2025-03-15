package com.trelloapp.dto;

import com.trelloapp.model.Task.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskDto {

    private Long id;

    @NotBlank(message = "Task title is required")
    private String title;

    private String description;

    @NotBlank(message = "List name is required")
    private String listName;

    private Integer priority;

    private LocalDateTime dueDate;

    @NotNull(message = "Board ID is required")
    private Long boardId;

    private TaskStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}