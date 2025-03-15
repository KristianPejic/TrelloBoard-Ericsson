package com.trelloapp.controller;

import com.trelloapp.dto.TaskDto;
import com.trelloapp.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boards/{boardId}/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    /**
     * Get all tasks for a board
     * 
     * @param boardId Board ID
     * @return List of task DTOs
     */
    @GetMapping
    public ResponseEntity<List<TaskDto>> getTasksByBoardId(@PathVariable Long boardId) {
        return ResponseEntity.ok(taskService.getTasksByBoardId(boardId));
    }

    /**
     * Get tasks by list name
     * 
     * @param boardId  Board ID
     * @param listName List name
     * @return List of task DTOs
     */
    @GetMapping("/list/{listName}")
    public ResponseEntity<List<TaskDto>> getTasksByListName(
            @PathVariable Long boardId,
            @PathVariable String listName) {
        return ResponseEntity.ok(taskService.getTasksByBoardIdAndListName(boardId, listName));
    }

    /**
     * Get a task by ID
     * 
     * @param boardId Board ID
     * @param taskId  Task ID
     * @return Task DTO
     */
    @GetMapping("/{taskId}")
    public ResponseEntity<TaskDto> getTaskById(
            @PathVariable Long boardId,
            @PathVariable Long taskId) {
        return ResponseEntity.ok(taskService.getTaskById(boardId, taskId));
    }

    /**
     * Create a new task
     * 
     * @param boardId Board ID
     * @param taskDto Task details
     * @return Created task DTO
     */
    @PostMapping
    public ResponseEntity<TaskDto> createTask(
            @PathVariable Long boardId,
            @Valid @RequestBody TaskDto taskDto) {
        taskDto.setBoardId(boardId); // Ensure board ID is set correctly
        return new ResponseEntity<>(taskService.createTask(taskDto), HttpStatus.CREATED);
    }

    /**
     * Update a task
     * 
     * @param boardId Board ID
     * @param taskId  Task ID
     * @param taskDto Updated task details
     * @return Updated task DTO
     */
    @PutMapping("/{taskId}")
    public ResponseEntity<TaskDto> updateTask(
            @PathVariable Long boardId,
            @PathVariable Long taskId,
            @Valid @RequestBody TaskDto taskDto) {
        return ResponseEntity.ok(taskService.updateTask(boardId, taskId, taskDto));
    }

    /**
     * Delete a task
     * 
     * @param boardId Board ID
     * @param taskId  Task ID
     * @return No content response
     */
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable Long boardId,
            @PathVariable Long taskId) {
        taskService.deleteTask(boardId, taskId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Move a task to a different list
     * 
     * @param boardId  Board ID
     * @param taskId   Task ID
     * @param listName New list name
     * @return Updated task DTO
     */
    @PatchMapping("/{taskId}/move")
    public ResponseEntity<TaskDto> moveTask(
            @PathVariable Long boardId,
            @PathVariable Long taskId,
            @RequestParam String listName) {
        return ResponseEntity.ok(taskService.moveTask(boardId, taskId, listName));
    }
}