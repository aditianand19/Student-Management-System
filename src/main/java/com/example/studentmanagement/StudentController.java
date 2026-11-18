
package com.example.studentmanagement;

import com.example.studentmanagement.service.StudentService;
import com.example.studentmanagement.exception.DuplicateEmailException;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    // Display all students
    @GetMapping("/")
    public String viewStudents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            Model model) {

        Page<Student> studentPage =
                studentService.getStudentsWithSorting(
                        page, size, sortBy, direction
                );

        model.addAttribute("students", studentPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", studentPage.getTotalPages());
        model.addAttribute("totalItems", studentPage.getTotalElements());
        model.addAttribute("totalCourses", studentService.getTotalCourses());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("direction", direction);
        model.addAttribute("name", "");
        model.addAttribute("course", "");
        addCoursesToModel(model);

        return "students";
    }

    // Show add student form
    @GetMapping("/add")
    public String addStudentForm(Model model) {
        model.addAttribute("student", new Student());
        return "add-student";
    }

    // Save student
    @PostMapping("/save")
    public String saveStudent(
            @Valid @ModelAttribute("student") Student student,
            BindingResult result,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "add-student";
        }

        try {
            studentService.saveStudent(student);
        } catch (DuplicateEmailException ex) {
            result.rejectValue(
                    "email", "duplicate",
                    "Email already registered. Please use another email."
            );
            return "add-student";
        }

        redirectAttributes.addFlashAttribute(
                "successMessage", "Student added successfully!"
        );

        return "redirect:/";
    }

    // Show edit form
    @GetMapping("/edit/{id}")
    public String editStudentForm(
            @PathVariable int id, Model model) {

        model.addAttribute(
                "student", studentService.getStudentById(id)
        );

        return "add-student";
    }

    // Update student
    @PostMapping("/update/{id}")
    public String updateStudent(
            @PathVariable int id,
            @Valid @ModelAttribute("student") Student student,
            BindingResult result,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "add-student";
        }

        try {
            studentService.updateStudent(id, student);
        } catch (DuplicateEmailException ex) {
            result.rejectValue(
                    "email", "duplicate",
                    "Email already registered. Please use another email."
            );
            return "add-student";
        }

        redirectAttributes.addFlashAttribute(
                "successMessage", "Student updated successfully!"
        );

        return "redirect:/";
    }

    // Delete student
    @PostMapping("/delete/{id}")
    public String deleteStudent(
            @PathVariable int id,
            RedirectAttributes redirectAttributes) {

        studentService.deleteStudent(id);

        redirectAttributes.addFlashAttribute(
                "successMessage", "Student deleted successfully!"
        );

        return "redirect:/";
    }

    // Search by student name
    @GetMapping("/search")
    public String searchStudents(
            @RequestParam(defaultValue = "") String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            Model model) {

        Page<Student> studentPage =
                studentService.searchStudentsWithSorting(
                        name, page, size, sortBy, direction
                );

        model.addAttribute("students", studentPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", studentPage.getTotalPages());
        model.addAttribute("totalItems", studentPage.getTotalElements());
        model.addAttribute("totalCourses", studentService.getTotalCourses());
        model.addAttribute("name", name);
        model.addAttribute("course", "");
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("direction", direction);
        addCoursesToModel(model);

        return "students";
    }

    // Filter by course and search by name
    @GetMapping("/filter")
    public String filterStudents(
            @RequestParam(defaultValue = "") String name,
            @RequestParam(defaultValue = "") String course,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            Model model) {

        Page<Student> studentPage =
                studentService.filterStudents(
                        name, course, page, size, sortBy, direction
                );

        model.addAttribute("students", studentPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", studentPage.getTotalPages());
        model.addAttribute("totalItems", studentPage.getTotalElements());
        model.addAttribute("totalCourses", studentService.getTotalCourses());
        model.addAttribute("name", name);
        model.addAttribute("course", course);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("direction", direction);
        addCoursesToModel(model);

        return "students";
    }

    // Add available courses to dropdown
    private void addCoursesToModel(Model model) {
        List<String> courses = studentService.getAllStudents()
                .stream()
                .map(Student::getCourse)
                .filter(course -> course != null && !course.isBlank())
                .distinct()
                .sorted()
                .toList();

        model.addAttribute("courses", courses);
    }
}
