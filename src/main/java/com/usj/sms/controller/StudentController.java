package com.usj.sms.controller;

import com.usj.sms.entity.Student;
import com.usj.sms.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;

    @Autowired
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    // 1. GET /api/students - Get All Students (200 OK) with pagination, sorting and optional search
    @GetMapping
    public ResponseEntity<Page<Student>> getAllStudents(
        @RequestParam(value = "name", required = false) String name,
        @RequestParam(value = "course", required = false) String course,
        @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC)
            Pageable pageable) {
    Page<Student> page = studentService.searchStudents(name, course, pageable);
    return new ResponseEntity<>(page, HttpStatus.OK);
    }

    // 2. POST /api/students - Add Student (201 CREATED)
    @PostMapping
    public ResponseEntity<Student> addStudent(@Valid @RequestBody Student student) {
        Student savedStudent = studentService.saveStudent(student);
        return new ResponseEntity<>(savedStudent, HttpStatus.CREATED);
    }

    // 3. GET /api/students/{id} - Get Student By ID (200 OK or 404 NOT FOUND)
    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable Long id) {
        return studentService.getStudentById(id)
            .map(student -> new ResponseEntity<>(student, HttpStatus.OK))
            .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // 4. PUT /api/students/{id} - Update Student (200 OK or 404 NOT FOUND)
    @PutMapping("/{id}")
    public ResponseEntity<Student> updateStudent(@PathVariable Long id, @Valid @RequestBody Student studentDetails) {
        Student updatedStudent = studentService.updateStudent(id, studentDetails);
        if (updatedStudent != null) {
            return new ResponseEntity<>(updatedStudent, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // 5. DELETE /api/students/{id} - Delete Student (204 NO CONTENT)
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
