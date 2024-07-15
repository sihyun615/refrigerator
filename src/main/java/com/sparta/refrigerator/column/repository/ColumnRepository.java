package com.sparta.refrigerator.column.repository;

import com.sparta.refrigerator.board.entity.Board;
import com.sparta.refrigerator.column.entity.Columns;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ColumnRepository extends JpaRepository<Columns, Long> {

    Optional<Columns> findByColumnName(String columnName);

    List<Columns> findAllByBoardOrderByColumnIndex(Board board);

    @Query("SELECT MAX(c.columnIndex) FROM Columns c WHERE c.board = :board")
    Long findMaxColumnIndexByBoard(@Param("board") Board board);


}
