package com.trelloapp.controller;

import com.trelloapp.dto.BoardDto;
import com.trelloapp.service.BoardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    /**
     * Get all boards for the current user
     * 
     * @return List of board DTOs
     */
    @GetMapping
    public ResponseEntity<List<BoardDto>> getAllBoards() {
        return ResponseEntity.ok(boardService.getAllBoards());
    }

    /**
     * Get a board by ID
     * 
     * @param boardId Board ID
     * @return Board DTO
     */
    @GetMapping("/{boardId}")
    public ResponseEntity<BoardDto> getBoardById(@PathVariable Long boardId) {
        return ResponseEntity.ok(boardService.getBoardById(boardId));
    }

    /**
     * Create a new board
     * 
     * @param boardDto Board details
     * @return Created board DTO
     */
    @PostMapping
    public ResponseEntity<BoardDto> createBoard(@Valid @RequestBody BoardDto boardDto) {
        return new ResponseEntity<>(boardService.createBoard(boardDto), HttpStatus.CREATED);
    }

    /**
     * Update a board
     * 
     * @param boardId  Board ID
     * @param boardDto Updated board details
     * @return Updated board DTO
     */
    @PutMapping("/{boardId}")
    public ResponseEntity<BoardDto> updateBoard(
            @PathVariable Long boardId,
            @Valid @RequestBody BoardDto boardDto) {
        return ResponseEntity.ok(boardService.updateBoard(boardId, boardDto));
    }

    /**
     * Delete a board
     * 
     * @param boardId Board ID
     * @return No content response
     */
    @DeleteMapping("/{boardId}")
    public ResponseEntity<Void> deleteBoard(@PathVariable Long boardId) {
        boardService.deleteBoard(boardId);
        return ResponseEntity.noContent().build();
    }
}