package ru.hogwarts.school.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/student")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable Long id) {
        Student student = studentService.findStudent(id);
        if (student == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(student);
    }

    @PostMapping
    public ResponseEntity<Student> createStudent(@RequestBody Student student) {
        Student createdStudent = studentService.addStudent(student);
        return ResponseEntity.ok(createdStudent);
    }

    @PutMapping("{id}")
    public ResponseEntity<Student> editStudent(@PathVariable Long id,@RequestBody Student student) {
        Student foundStudent = studentService.editStudent(id, student);
        if (foundStudent == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(foundStudent);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.ok().build();
    }

    //Найти по возрасту
    @GetMapping
    public ResponseEntity<Collection<Student>> findStudentsByAge(@RequestParam(required = false) Integer age) {
        if (age > 0) {
            return ResponseEntity.ok(studentService.findByAge(age));
        }
        return ResponseEntity.ok(Collections.emptyList());
    }

    //Найти по диапазону возраста
    @GetMapping("/by-age")
    public List<Student> getStudentsByAgeRange(@RequestParam int min, @RequestParam int max) {
        return studentService.findStudentsByAgeRange(min, max);
    }

    //Найти студента по факультету
    @GetMapping("/{id}/faculty")
    public ResponseEntity<Faculty> findStudentFaculty(@PathVariable Long id) {
        Faculty faculty = studentService.getFacultyByStudentId(id);
        return faculty != null ? ResponseEntity.ok(faculty) : ResponseEntity.notFound().build();
    }

    @GetMapping("/count")
    public ResponseEntity<Integer> countAllStudents() {
        Integer count = studentService.countAllStudents();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/avg-age")
    public ResponseEntity<Double> findAverageAge() {
        Double avgAge = studentService.findAverageAge();
        return ResponseEntity.ok(avgAge);
    }

    @GetMapping("/last-five")
    public ResponseEntity<List<Student>> findLastFiveStudents() {
        List<Student> students = studentService.findLastFiveStudents();
        return ResponseEntity.ok(students);
    }
}
