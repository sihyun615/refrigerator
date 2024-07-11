package com.sparta.refrigerator.column.repository;

import com.sparta.refrigerator.auth.entity.User;
import com.sparta.refrigerator.board.entity.Board;
import com.sparta.refrigerator.column.entity.Column;
import com.sparta.refrigerator.column.entity.StatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ColumnRepository extends JpaRepository<Column,Long> {
    Optional<Column> findByStatus(StatusEnum status);

    List<Column> findAllByOrderByColumnIndex();

    List<Column> findAllByBoard(Board board);
}
