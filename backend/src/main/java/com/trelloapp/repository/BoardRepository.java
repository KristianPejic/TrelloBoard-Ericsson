package com.trelloapp.repository;

import com.trelloapp.model.Board;
import com.trelloapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
    List<Board> findByOwner(User owner);

    Optional<Board> findByIdAndOwner(Long boardId, User owner);
}