package com.sparta.refrigerator.board.repository;

import com.sparta.refrigerator.auth.entity.User;
import com.sparta.refrigerator.auth.enumeration.UserAuth;
import com.sparta.refrigerator.board.entity.Board;
import com.sparta.refrigerator.board.entity.Invitation;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvitationRepository extends JpaRepository <Invitation, Long> {
    boolean existsByBoardAndUser(Board board, User user);
}
