package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class StudentService {
    private final Logger logger = LoggerFactory.getLogger(StudentService.class);
    private final StudentRepository studentRepository;

    @Autowired
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }


    public Student addStudent(Student student) {
        logger.info("Was invoked method for create student");
        return studentRepository.save(student);
    }

    public Student findStudent(long id) {
        logger.debug("Was invoked method for find student with id = {}", id);
        return studentRepository.findById(id).orElse(null);
    }

    public Student editStudent(long id, Student student) {
        logger.info("Was invoked method for edit student with id = {}", id);
        if (!studentRepository.existsById(student.getId())) {
            logger.error("Student not found with id = {}", id);
            return null;
        }
        return studentRepository.save(student);
    }

    public void deleteStudent(long id) {
        logger.warn("Was invoked method for delete student with id = {}", id);
        studentRepository.deleteById(id);
    }

    public Collection<Student> findByAge(int age) {
        logger.debug("Was invoked method for find student by age = {}", age);
        return studentRepository.findByAge(age);
    }

    public List<Student> findStudentsByAgeRange(int min, int max) {
        logger.debug("Was invoked method for find student by age in the range from = {} to = {}", min, max);
        return studentRepository.findByAgeBetween(min, max);
    }

    public Faculty getFacultyByStudentId(Long studentId) {
        logger.debug("Was invoked method for find  faculty by student id = {}", studentId);
    public Faculty getFacultyByStudentId (Long studentId) {
      master
        return studentRepository.findById(studentId)
                .map(Student::getFaculty)
                .orElse(null);
    }

    public Integer countAllStudents() {
        logger.debug("Was invoked method for get number of students");
        return studentRepository.countAllStudents();
    }

    public Double findAverageAge() {
        logger.debug("Was invoked method for find average age students");
        return studentRepository.findAverageAge();
    }

    public List<Student> findLastFiveStudents() {
        logger.debug("Was invoked method for find last five students");
        return studentRepository.findLastFiveStudents();
    }
    public List<String> getStudentNamesStartingWithA() {
        logger.debug("Was invoked method for find students names starts with 'A'");
        return studentRepository.findAll().stream()
                .map(Student::getName)
                .filter(name -> name.startsWith("A") || name.startsWith("А"))
                .map(String::toUpperCase)
                .sorted()
                .collect(Collectors.toList());
    }

    public Double findAverageAgeStream() {
        logger.debug("Was invoked method for find last five students(use Stream API)");
        return studentRepository.findAll().stream()
                .mapToInt(Student::getAge)
                .average()
                .orElse(0.0);
    }

    public void printStudentsParallel() {
        logger.debug("Was invoked method for parallel printing of students");
        List<Student> students = studentRepository.findAll();

        if (students.size() < 6) {
            logger.error("Not enough students in database (students found {})", students.size());
            throw new RuntimeException("Need at least 6 students in database");
        }

        System.out.println(students.get(0).getName());
        System.out.println(students.get(1).getName());

        Thread thread1 = new Thread(() -> {
            System.out.println(students.get(2).getName());
            System.out.println(students.get(3).getName());
        });

        Thread thread2 = new Thread(() -> {
            System.out.println(students.get(4).getName());
            System.out.println(students.get(5).getName());
        });

        thread1.start();
        thread2.start();

        // Ждём завершения потоков
        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread execution interrupted", e);
        }
    }

    // Синхронизированный метод для вывода
    private synchronized void printStudentName(String name) {
        System.out.println(name);
    }

    public void printStudentsSynchronized() {
        logger.debug("Was invoked method for synchronized printing of students");

        List<Student> students = studentRepository.findAll();

        if (students.size() < 6) {
            logger.error("Not enough students in database (students found {})", students.size());
            throw new RuntimeException("Need at least 6 students in database");
        }

        printStudentName(students.get(0).getName());
        printStudentName(students.get(1).getName());

        Thread thread1 = new Thread(() -> {
            printStudentName(students.get(2).getName());
            printStudentName(students.get(3).getName());
        });

        Thread thread2 = new Thread(() -> {
            printStudentName(students.get(4).getName());
            printStudentName(students.get(5).getName());
        });
        thread1.start();
        thread2.start();

        // Ждём завершения потоков
        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread execution interrupted", e);
        }
    }
}

