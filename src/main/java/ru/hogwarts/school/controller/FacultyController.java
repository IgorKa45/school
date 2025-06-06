package ru.hogwarts.school.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.FacultyService;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/faculty")
public class FacultyController {
    private final FacultyService facultyService;

    public FacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    @GetMapping("{id}")
    public ResponseEntity<Faculty> getFacultyById(@PathVariable Long id) {
        Faculty faculty = facultyService.findFaculty(id);
        if (faculty == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(faculty);
    }

    @PostMapping
    public Faculty createFaculty(@RequestBody Faculty faculty) {
        return facultyService.addFaculty(faculty);
    }

    @PutMapping("{id}")
    public ResponseEntity<Faculty> editFaculty(@PathVariable Long id, @RequestBody Faculty faculty) {
        Faculty foundFaculty = facultyService.editFaculty(id, faculty);
        if (foundFaculty == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(foundFaculty);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteFaculty(@PathVariable Long id) {
        facultyService.deleteFaculty(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Collection<Faculty>> findFacultiesByColor(@RequestParam(required = false) String color) {
        if (color != null && !color.isBlank()) {
            return ResponseEntity.ok(facultyService.findByColor(color));
        }
        return ResponseEntity.ok(Collections.emptyList());
    }

    @GetMapping("/search")
    public List<Faculty> findFacultiesByNameOrColor(@RequestParam String query) {
        return facultyService.findFacultiesByNameOrColor(query);
    }
    @GetMapping("/{id}/students")
    public ResponseEntity<List<Student>> getStudentsByFacultyId(@PathVariable Long id) {
        System.out.println("Получен запрос GET /faculty/" + id + "/students");

        List<Student> students = facultyService.getStudentsByFacultyId(id);
        if (students == null || students.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        System.out.println("Отправляем список студентов: " + students.size());
        return ResponseEntity.ok(students);
    }

    @GetMapping("/longest-name")
    public ResponseEntity<String> getLongestFacultyName() {
        String longestName = facultyService.getLongestFacultyName();
        return ResponseEntity.ok(longestName);
    }

    @GetMapping("/sum")
    public ResponseEntity<Integer> calculateSum() {
        int sum = facultyService.calculateSum();
        return ResponseEntity.ok(sum);
    }
}
