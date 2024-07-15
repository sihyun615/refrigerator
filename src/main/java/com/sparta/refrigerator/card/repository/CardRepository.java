package com.sparta.refrigerator.card.repository;

import com.sparta.refrigerator.card.entity.Card;
import com.sparta.refrigerator.column.entity.Columns;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CardRepository extends JpaRepository<Card, Long> {

    List<Card> findByColumns(Columns columns);

    List<Card> findAllByCollaboratorAndBoardId(String collaborator, Long boardId);

    List<Card> findAllByBoardId(Long boardId);

    @Query("SELECT MAX(c.cardIndex) FROM Card c WHERE c.columns = :columns")
    Long findMaxCardIndexByColumns(@Param("columns") Columns columns);


}
