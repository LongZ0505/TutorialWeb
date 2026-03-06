package com.tutor.project.repository;

import com.tutor.project.entity.InvalidToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvalidationToken extends JpaRepository<InvalidToken,String> {
}
