package com.sparta.refrigerator.column.repository;

import com.sparta.refrigerator.column.entity.Column;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ColumnRepository extends JpaRepository<Column,Long> {
}
