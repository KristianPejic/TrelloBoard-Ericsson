package com.trelloapp.service;

import com.trelloapp.dto.BoardDto;
import com.trelloapp.exception.ResourceNotFoundException;
import com.trelloapp.exception.UnauthorizedException;
import com.trelloapp.model.Board;
import com.trelloapp.model.User;
import com.trelloapp.repository.BoardRepository;
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
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    /**
     * Get current authenticated user
     * Uses Singleton pattern through SecurityContextHolder
     */
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UnauthorizedException("User not found"));
    }

    /**
     * Convert Board entity to BoardDto
     * Uses Adapter pattern for entity-to-DTO conversion
     */
    private BoardDto mapToDto(Board board) {
        return BoardDto.builder()
                .id(board.getId())
                .name(board.getName())
                .description(board.getDescription())
                .createdAt(board.getCreatedAt())
                .updatedAt(board.getUpdatedAt())
                .build();
    }

    /**
     * Get all boards for current user
     */
    public List<BoardDto> getAllBoards() {
        User currentUser = getCurrentUser();
        return boardRepository.findByOwner(currentUser).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    /**
     * Get a specific board by ID
     */
    public BoardDto getBoardById(Long boardId) {
        User currentUser = getCurrentUser();
        Board board = boardRepository.findByIdAndOwner(boardId, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Board not found with id: " + boardId));

        return mapToDto(board);
    }

    /**
     * Create a new board
     * Uses Factory pattern for creating Board entity
     */
    public BoardDto createBoard(BoardDto boardDto) {
        User currentUser = getCurrentUser();

        Board board = Board.builder()
                .name(boardDto.getName())
                .description(boardDto.getDescription())
                .owner(currentUser)
                .build();

        Board savedBoard = boardRepository.save(board);
        return mapToDto(savedBoard);
    }

    /**
     * Update an existing board
     * Uses Command pattern for update operation
     */
    @Transactional
    public BoardDto updateBoard(Long boardId, BoardDto boardDto) {
        User currentUser = getCurrentUser();

        Board board = boardRepository.findByIdAndOwner(boardId, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Board not found with id: " + boardId));

        // Update board details
        board.setName(boardDto.getName());
        board.setDescription(boardDto.getDescription());

        Board updatedBoard = boardRepository.save(board);
        return mapToDto(updatedBoard);
    }

    /**
     * Delete a board
     */
    public void deleteBoard(Long boardId) {
        User currentUser = getCurrentUser();

        Board board = boardRepository.findByIdAndOwner(boardId, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Board not found with id: " + boardId));

        boardRepository.delete(board);
    }
}