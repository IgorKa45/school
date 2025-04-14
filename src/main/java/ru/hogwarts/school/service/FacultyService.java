package ru.hogwarts.school.service;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.*;

@Service
public class FacultyService {
    private final Logger logger = LoggerFactory.getLogger(FacultyService.class);
    private final FacultyRepository facultyRepository;

    @Autowired
    public FacultyService(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    public Faculty addFaculty(Faculty faculty) {
        logger.info("Was invoked method for create faculty");
        return facultyRepository.save(faculty);
    }

    public Faculty findFaculty(long id) {
        logger.debug("Was invoked method for find faculty by id = {}", id);
        return facultyRepository.findById(id).orElse(null);
    }

    public Faculty editFaculty(long id, Faculty faculty) {
        logger.info("Was invoked method for edit faculty by id = {}", id);
        if (!facultyRepository.existsById(faculty.getId())) {
            throw new EntityNotFoundException("Faculty with id " + id + " not found");
        }
        return facultyRepository.save(faculty);
    }

    public void deleteFaculty(long id) {
        logger.warn("Was invoked method for delete faculty by id = {}", id);
        facultyRepository.deleteById(id);
    }

    public Collection<Faculty> findByColor(String color) {
        logger.info("Was invoked method for find faculty by color = {}", color);
        return facultyRepository.findByColor(color);
    }

    public List<Faculty> findFacultiesByNameOrColor(String query) {
        logger.debug("Was invoked method for find faculty by color or name = {}", query);
        return facultyRepository.findByNameIgnoreCaseOrColorIgnoreCase(query, query);
    }

    public List<Student> getStudentsByFacultyId(Long facultyId) {
        logger.debug("Was invoked method for find all students by faculty с id = {}", facultyId);

        Faculty faculty = facultyRepository.findFacultyWithStudentsById(facultyId);


        if (faculty == null) {
            logger.warn("Faculty not found with id = {}", facultyId);
            return Collections.emptyList(); // Или выбросить 404 в контроллере
        }

        List<Student> students = faculty.getStudents();
        logger.debug("Found faculty: {}, students count: {}", faculty.getName(), (students != null ? students.size() : 0));
        return students != null ? students : Collections.emptyList();
    }
}
