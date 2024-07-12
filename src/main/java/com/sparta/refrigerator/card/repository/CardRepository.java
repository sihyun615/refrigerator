package com.sparta.refrigerator.card.repository;

import com.sparta.refrigerator.card.entity.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card, Long> {

    Page<Card> findAllByColumnId(Long columnId, Pageable pageable);

}
