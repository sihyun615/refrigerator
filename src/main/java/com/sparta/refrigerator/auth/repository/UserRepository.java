package com.sparta.refrigerator.auth.repository;

import com.sparta.refrigerator.auth.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findByUserName(String userName);

}
