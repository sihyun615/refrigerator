package com.sparta.refrigerator.card.repository;

import com.sparta.refrigerator.auth.entity.User;
import com.sparta.refrigerator.card.entity.Card;
import com.sparta.refrigerator.column.entity.Columns;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card, Long> {
    List<Card> findByUser(User user);

    List<Card> findByColumns(Columns columns);

}
