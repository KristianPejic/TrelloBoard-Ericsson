package com.trelloapp.service;

import com.trelloapp.dto.TaskDto;
import com.trelloapp.exception.ResourceNotFoundException;
import com.trelloapp.exception.UnauthorizedException;
import com.trelloapp.model.Board;
import com.trelloapp.model.Task;
import com.trelloapp.model.User;
import com.trelloapp.repository.BoardRepository;
import com.trelloapp.repository.TaskRepository;
import com.trelloapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    /**
     * Get current authenticated user
     */
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UnauthorizedException("User not found"));
    }

    /**
     * Get board by ID ensuring current user is the owner
     */
    private Board getBoardByIdAndCurrentUser(Long boardId) {
        User currentUser = getCurrentUser();
        return boardRepository.findByIdAndOwner(boardId, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Board not found with id: " + boardId));
    }

    /**
     * Convert Task entity to TaskDto
     * Uses Adapter pattern for entity-to-DTO conversion
     */
    private TaskDto mapToDto(Task task) {
        return TaskDto.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .listName(task.getListName())
                .priority(task.getPriority())
                .dueDate(task.getDueDate())
                .boardId(task.getBoard().getId())
                .status(task.getStatus())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }

    /**
     * Get all tasks for a board
     */
    public List<TaskDto> getTasksByBoardId(Long boardId) {
        Board board = getBoardByIdAndCurrentUser(boardId);

        return taskRepository.findByBoard(board).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    /**
     * Get tasks by board ID and list name
     */
    public List<TaskDto> getTasksByBoardIdAndListName(Long boardId, String listName) {
        Board board = getBoardByIdAndCurrentUser(boardId);

        return taskRepository.findByBoardAndListName(board, listName).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    /**
     * Get a specific task by ID
     */
    public TaskDto getTaskById(Long boardId, Long taskId) {
        Board board = getBoardByIdAndCurrentUser(boardId);

        Task task = taskRepository.findByIdAndBoard(taskId, board)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));

        return mapToDto(task);
    }

    /**
     * Create a new task
     * Uses Factory pattern for creating Task entity
     */
    public TaskDto createTask(TaskDto taskDto) {
        Board board = getBoardByIdAndCurrentUser(taskDto.getBoardId());

        Task task = Task.builder()
                .title(taskDto.getTitle())
                .description(taskDto.getDescription())
                .listName(taskDto.getListName())
                .priority(taskDto.getPriority())
                .dueDate(taskDto.getDueDate())
                .board(board)
                .status(Task.TaskStatus.PENDING)
                .build();

        Task savedTask = taskRepository.save(task);
        return mapToDto(savedTask);
    }

    /**
     * Update an existing task
     * Uses Command pattern for update operation
     */
    @Transactional
    public TaskDto updateTask(Long boardId, Long taskId, TaskDto taskDto) {
        Board board = getBoardByIdAndCurrentUser(boardId);

        Task task = taskRepository.findByIdAndBoard(taskId, board)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));

        // Update task details
        task.setTitle(taskDto.getTitle());
        task.setDescription(taskDto.getDescription());
        task.setListName(taskDto.getListName());
        task.setPriority(taskDto.getPriority());
        task.setDueDate(taskDto.getDueDate());
        task.setStatus(taskDto.getStatus());

        Task updatedTask = taskRepository.save(task);
        return mapToDto(updatedTask);
    }

    /**
     * Delete a task
     */
    public void deleteTask(Long boardId, Long taskId) {
        Board board = getBoardByIdAndCurrentUser(boardId);

        Task task = taskRepository.findByIdAndBoard(taskId, board)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));

        taskRepository.delete(task);
    }

    /**
     * Move a task to a different list
     * Uses Command pattern for specific operation
     */
    @Transactional
    public TaskDto moveTask(Long boardId, Long taskId, String newListName) {
        Board board = getBoardByIdAndCurrentUser(boardId);

        Task task = taskRepository.findByIdAndBoard(taskId, board)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));

        task.setListName(newListName);
        Task updatedTask = taskRepository.save(task);

        return mapToDto(updatedTask);
    }
}
