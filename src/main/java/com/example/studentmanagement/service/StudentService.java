
package com.example.studentmanagement.service;

import com.example.studentmanagement.Student;
import com.example.studentmanagement.StudentRepository;
import com.example.studentmanagement.exception.DuplicateEmailException;
import com.example.studentmanagement.exception.StudentNotFoundException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    // Add Student
    public Student saveStudent(Student student) {
        if (studentRepository.existsByEmail(student.getEmail())) {
            throw new DuplicateEmailException(
                    "Email already registered: " + student.getEmail()
            );
        }
        return studentRepository.save(student);
    }

    // Get All Students
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    // Get Student By ID
    public Student getStudentById(int id) {
        return studentRepository.findById(id)
                .orElseThrow(() ->
                        new StudentNotFoundException(
                                "Student not found with ID: " + id
                        )
                );
    }

    // Update Student
    public Student updateStudent(int id, Student student) {
        Student existingStudent = getStudentById(id);

        if (studentRepository.existsByEmailAndIdNot(
                student.getEmail(), id)) {
            throw new DuplicateEmailException(
                    "Email already registered: " + student.getEmail()
            );
        }

        existingStudent.setName(student.getName());
        existingStudent.setEmail(student.getEmail());
        existingStudent.setCourse(student.getCourse());

        return studentRepository.save(existingStudent);
    }

    // Delete Student
    public void deleteStudent(int id) {
        Student student = getStudentById(id);
        studentRepository.delete(student);
    }

    // Search Students
    public List<Student> searchStudents(String name) {
        return studentRepository.findByNameContainingIgnoreCase(name);
    }

    // Pagination
    public Page<Student> getStudentsWithPagination(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return studentRepository.findAll(pageable);
    }

    // Search with Pagination
    public Page<Student> searchStudentsWithPagination(
            String name, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        return studentRepository.findByNameContainingIgnoreCase(
                name, pageable
        );
    }

    // Pagination with Sorting
    public Page<Student> getStudentsWithSorting(
            int page, int size, String sortBy, String direction) {

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        return studentRepository.findAll(pageable);
    }

    // Search with Pagination and Sorting
    public Page<Student> searchStudentsWithSorting(
            String name, int page, int size,
            String sortBy, String direction) {

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return studentRepository.findByNameContainingIgnoreCase(
                name, pageable
        );
    }

    // Course Filtering with Search, Pagination and Sorting
    public Page<Student> filterStudents(
            String name, String course,
            int page, int size,
            String sortBy, String direction) {

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return studentRepository.searchByNameAndCourse(
                name, course, pageable
        );
    }

    // Get Total Unique Courses
    public long getTotalCourses() {
        return studentRepository.countDistinctCourses();
    }
}
