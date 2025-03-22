package ru.hogwarts.school.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.*;

@Service
public class FacultyService {

    private final FacultyRepository facultyRepository;
    @Autowired
    public FacultyService(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    public Faculty addFaculty(Faculty faculty) {
        return facultyRepository.save(faculty);
    }

    public Faculty findFaculty(long id) {
        return facultyRepository.findById(id).orElse(null);
    }

    public Faculty editFaculty(long id, Faculty faculty) {
        if (!facultyRepository.existsById(faculty.getId())) {
            throw new EntityNotFoundException("Faculty with id " + id + " not found");
        }
        return facultyRepository.save(faculty);
    }

    public void deleteFaculty(long id) {
        facultyRepository.deleteById(id);
    }

    public Collection<Faculty> findByColor(String color) {
        return facultyRepository.findByColor(color);
    }

    public List<Faculty> findFacultiesByNameOrColor(String query) {
        return facultyRepository.findByNameIgnoreCaseOrColorIgnoreCase(query, query);
    }

    public List<Student> getStudentsByFacultyId(Long facultyId) {
        System.out.println("Проверяем факультет с ID: " + facultyId);

        Faculty faculty = facultyRepository.findFacultyWithStudentsById(facultyId);


        if (faculty == null) {
            System.out.println("Факультет не найден!");
            return Collections.emptyList(); // Или выбросить 404 в контроллере
        }

        List<Student> students = faculty.getStudents();
        System.out.println("Найден факультет: " + faculty.getName() + ", студенты: " + (students != null ? students.size() : "null"));

        return students != null ? students : Collections.emptyList();
    }
}
