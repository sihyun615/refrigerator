package com.sparta.refrigerator.card.repository;

import com.sparta.refrigerator.auth.entity.User;
import com.sparta.refrigerator.card.entity.Card;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card, Long> {
    List<Card> findAllByUser_Id(Long userId);

}
