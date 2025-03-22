package ru.hogwarts.school.controller.testRestTemplate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.hogwarts.school.model.Student;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class StudentControllerTest {
    @Autowired
    private TestRestTemplate restTemplate;

    private final String BASE_URL = "/student";

    @Test
    void testCreateStudent() {
        Student student = new Student();
        student.setName("Harry Potter");
        student.setAge(17);

        ResponseEntity<Student> response = restTemplate.postForEntity(BASE_URL, student, Student.class);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals("Harry Potter", response.getBody().getName());
    }

    @Test
    void testGetStudentById() {
        Student student = new Student();
        student.setName("Harry Potter");
        student.setAge(17);

        ResponseEntity<Student> createdResponse = restTemplate.postForEntity(BASE_URL, student, Student.class);
        Assertions.assertEquals(HttpStatus.OK, createdResponse.getStatusCode());
        Assertions.assertNotNull(createdResponse.getBody());

        Long studentId = createdResponse.getBody().getId();


        ResponseEntity<Student> response = restTemplate.getForEntity(BASE_URL + "/" + studentId, Student.class);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals("Harry Potter", response.getBody().getName());
    }

    @Test
    void testFindStudentsByAge() {
        ResponseEntity<Student[]> response = restTemplate.getForEntity(BASE_URL + "?age=18", Student[].class);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
    }

    @Test
    void testUpdateStudent() {
        Student newStudent = new Student();
        newStudent.setName("Ron Weasley");
        newStudent.setAge(17);

        ResponseEntity<Student> createdResponse = restTemplate.postForEntity(BASE_URL, newStudent, Student.class);
        Assertions.assertEquals(HttpStatus.OK, createdResponse.getStatusCode());
        Assertions.assertNotNull(createdResponse.getBody());

        Long studentId = createdResponse.getBody().getId();


        Student updatedStudent = new Student();
        updatedStudent.setId(studentId);
        updatedStudent.setName("Hermione Granger");
        updatedStudent.setAge(18);

        HttpEntity<Student> request = new HttpEntity<>(updatedStudent);
        ResponseEntity<Student> response = restTemplate.exchange(BASE_URL + "/" + studentId, HttpMethod.PUT, request, Student.class);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals("Hermione Granger", response.getBody().getName());
    }

    @Test
    void testDeleteStudent() {
        restTemplate.delete(BASE_URL + "/1");

        ResponseEntity<Student> response = restTemplate.getForEntity(BASE_URL + "/1", Student.class);
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
