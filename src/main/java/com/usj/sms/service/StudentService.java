package com.usj.sms.service;

import com.usj.sms.entity.Student;
import com.usj.sms.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    // Dependency Injection (DI)
    @Autowired
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    // 1. CREATE
    public Student saveStudent(Student student) {
        return studentRepository.save(student); 
    }

    // 2. READ ALL
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    // Read all with pagination & sorting
    public Page<Student> getAllStudents(Pageable pageable) {
        return studentRepository.findAll(pageable);
    }

    // 3. READ BY ID
    public Optional<Student> getStudentById(Long id) {
        return studentRepository.findById(id);
    }

    // SEARCH by name and/or course with pagination & sorting
    public Page<Student> searchStudents(String name, String course, Pageable pageable) {
        boolean hasName = name != null && !name.trim().isEmpty();
        boolean hasCourse = course != null && !course.trim().isEmpty();

        if (hasName && hasCourse) {
            // Use AND semantics when both name and course are provided
            return studentRepository.findByNameContainingIgnoreCaseAndCourseContainingIgnoreCase(name.trim(), course.trim(), pageable);
        } else if (hasName) {
            return studentRepository.findByNameContainingIgnoreCase(name.trim(), pageable);
        } else if (hasCourse) {
            return studentRepository.findByCourseContainingIgnoreCase(course.trim(), pageable);
        } else {
            return studentRepository.findAll(pageable);
        }
    }
    
    // 4. UPDATE (PUT) - Implementation added
    public Student updateStudent(Long id, Student studentDetails) {
        // Use findById() to check for existence
        return studentRepository.findById(id).map(student -> {
            // Update the fields of the existing student object
            student.setName(studentDetails.getName());
            student.setEmail(studentDetails.getEmail());
            student.setCourse(studentDetails.getCourse());
            student.setAge(studentDetails.getAge());
            return studentRepository.save(student); // Save the updated student
        }).orElse(null); // Return null if not found (Controller will handle 404)
    }

    // 5. DELETE
    public void deleteStudent(Long id) {
        studentRepository.deleteById(id);
    }
}
