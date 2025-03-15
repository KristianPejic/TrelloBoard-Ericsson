package com.trelloapp.repository;

import com.trelloapp.model.Board;
import com.trelloapp.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByBoard(Board board);

    List<Task> findByBoardAndListName(Board board, String listName);

    Optional<Task> findByIdAndBoard(Long taskId, Board board);
}