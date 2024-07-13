package com.sparta.refrigerator.comment.repository;

import com.sparta.refrigerator.card.entity.Card;
import com.sparta.refrigerator.comment.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findAllByCard(Card card, Pageable pageable);
}