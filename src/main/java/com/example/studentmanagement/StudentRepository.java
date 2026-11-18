
package com.example.studentmanagement;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {

    // Check duplicate email
    boolean existsByEmail(String email);

    // Check duplicate email while updating
    boolean existsByEmailAndIdNot(String email, Integer id);

    // Search by student name
    List<Student> findByNameContainingIgnoreCase(String name);

    // Search with pagination
    Page<Student> findByNameContainingIgnoreCase(
            String name, Pageable pageable
    );

    // Search by name and filter by course
    @Query("SELECT s FROM Student s WHERE " +
            "LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%')) " +
            "AND (:course = '' OR LOWER(s.course) = LOWER(:course))")
    Page<Student> searchByNameAndCourse(
            @Param("name") String name,
            @Param("course") String course,
            Pageable pageable
    );

    // Count distinct courses
    @Query("SELECT COUNT(DISTINCT s.course) FROM Student s")
    long countDistinctCourses();
}
