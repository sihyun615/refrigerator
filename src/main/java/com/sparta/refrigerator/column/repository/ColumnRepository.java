package com.sparta.refrigerator.column.repository;

import com.sparta.refrigerator.board.entity.Board;
import com.sparta.refrigerator.column.entity.Columns;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ColumnRepository extends JpaRepository<Columns, Long> {

    Optional<Object> findByColumnName(String columnName);

    List<Columns> findAllByBoardOrderByColumnIndex(Board board);

    Long findMaxColumnIndexByBoard(Board board);
}
