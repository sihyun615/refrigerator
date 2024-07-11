package com.sparta.refrigerator.comment.repository;

import com.sparta.refrigerator.comment.entity.Comment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByCardIdOrderByCreatedAtDesc(Long cardId);
}