package com.sparta.refrigerator.column.repository;

import com.sparta.refrigerator.auth.entity.User;
import com.sparta.refrigerator.column.entity.Column;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ColumnRepository extends JpaRepository<Column,Long> {
    Optional<Column> findByIdAndUser(Long id, User user);
    Optional<Column> findByColumnName(String columnName);
}
