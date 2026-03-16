package com.tutor.project.repository;

import com.tutor.project.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubjectRepository extends JpaRepository<Subject,String> {
    Optional<Subject> findByName(String name);
}
