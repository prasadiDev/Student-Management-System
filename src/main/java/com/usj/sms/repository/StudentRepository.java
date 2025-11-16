package com.usj.sms.repository;

import com.usj.sms.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    // Basic CRUD operations are inherited: save(), findAll(), findById(), deleteById(), etc.
    // Search by name containing (case-insensitive)
    org.springframework.data.domain.Page<com.usj.sms.entity.Student> findByNameContainingIgnoreCase(String name, org.springframework.data.domain.Pageable pageable);

    // Search by course containing (case-insensitive)
    org.springframework.data.domain.Page<com.usj.sms.entity.Student> findByCourseContainingIgnoreCase(String course, org.springframework.data.domain.Pageable pageable);

    // Search where name OR course matches (case-insensitive)
    org.springframework.data.domain.Page<com.usj.sms.entity.Student> findByNameContainingIgnoreCaseOrCourseContainingIgnoreCase(String name, String course, org.springframework.data.domain.Pageable pageable);

    // Search where name AND course match (case-insensitive)
    org.springframework.data.domain.Page<com.usj.sms.entity.Student> findByNameContainingIgnoreCaseAndCourseContainingIgnoreCase(String name, String course, org.springframework.data.domain.Pageable pageable);
}
