package com.sparta.refrigerator.column.repository;

import com.sparta.refrigerator.board.entity.Board;
import com.sparta.refrigerator.column.entity.Column;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ColumnRepository extends JpaRepository<Column,Long> {
    Optional<Object> findByColumnName(String columnName);

    List<Column> findAllByBoardOrderByColumnIndex(Board board);

    Long findMaxColumnIndexByBoard(Board board);
}
