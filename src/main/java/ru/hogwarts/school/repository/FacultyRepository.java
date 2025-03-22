package ru.hogwarts.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.hogwarts.school.model.Faculty;

import java.util.Collection;
import java.util.List;

@Repository
public interface FacultyRepository extends JpaRepository<Faculty, Long> {
    Collection<Faculty> findByColor(String color);

    List<Faculty> findByNameIgnoreCaseOrColorIgnoreCase(String name, String color);

    @Query("SELECT f FROM Faculty f LEFT JOIN FETCH f.students WHERE f.id = :facultyId")
    Faculty findFacultyWithStudentsById(@Param("facultyId") Long facultyId);

    default Faculty findFacultyWithStudentsByIdWithLogging(Long facultyId) {
        System.out.println("Выполняем запрос для факультета с ID: " + facultyId);
        Faculty faculty = findFacultyWithStudentsById(facultyId);
        if (faculty == null) {
            System.out.println("Факультет с ID " + facultyId + " не найден.");
        } else {
            System.out.println("Факультет найден: " + faculty.getName());
        }
        return faculty;
    }
}
