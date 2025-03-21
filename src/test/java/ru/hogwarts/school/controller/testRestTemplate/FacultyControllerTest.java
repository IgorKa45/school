package ru.hogwarts.school.controller.testRestTemplate;


import jakarta.transaction.Transactional;
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
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;



@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class FacultyControllerTest {
    @Autowired
    private TestRestTemplate restTemplate;


    private final String BASE_URL = "/faculty";

    @Test
    void testCreateFaculty() {
        Faculty newFaculty = new Faculty();
        newFaculty.setName("Gryffindor");
        newFaculty.setColor("Red");

        ResponseEntity<Faculty> response = restTemplate.postForEntity(BASE_URL, newFaculty, Faculty.class);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals("Gryffindor", response.getBody().getName());
    }

    @Test
    void testGetFacultyInfo() {
        Faculty newFaculty = new Faculty();
        newFaculty.setName("Hufflepuff");
        newFaculty.setColor("Yellow");

        Faculty createdFaculty = restTemplate.postForEntity(BASE_URL, newFaculty, Faculty.class).getBody();
        Assertions.assertNotNull(createdFaculty);

        ResponseEntity<Faculty> response = restTemplate.getForEntity(BASE_URL + "/" + createdFaculty.getId(), Faculty.class);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals("Hufflepuff", response.getBody().getName());
    }

    @Test
    void testEditFaculty() {
        Faculty newFaculty = new Faculty();
        newFaculty.setName("Ravenclaw");
        newFaculty.setColor("Blue");

        Faculty createdFaculty = restTemplate.postForEntity(BASE_URL, newFaculty, Faculty.class).getBody();
        Assertions.assertNotNull(createdFaculty);

        Faculty updatedFaculty = new Faculty();
        updatedFaculty.setId(createdFaculty.getId());
        updatedFaculty.setName("Slytherin");
        updatedFaculty.setColor("Green");

        HttpEntity<Faculty> request = new HttpEntity<>(updatedFaculty);
        ResponseEntity<Faculty> response = restTemplate.exchange(BASE_URL + "/" + createdFaculty.getId(), HttpMethod.PUT, request, Faculty.class);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals("Slytherin", response.getBody().getName());
    }

    @Test
    void testDeleteFaculty() {
        Faculty newFaculty = new Faculty();
        newFaculty.setName("Ravenclaw");
        newFaculty.setColor("Blue");

        Faculty createdFaculty = restTemplate.postForEntity(BASE_URL, newFaculty, Faculty.class).getBody();
        Assertions.assertNotNull(createdFaculty);

        ResponseEntity<Void> response = restTemplate.exchange(BASE_URL + "/" + createdFaculty.getId(), HttpMethod.DELETE, null, Void.class);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

        // Проверяем, что факультета больше нет
        ResponseEntity<Faculty> getResponse = restTemplate.getForEntity(BASE_URL + "/" + createdFaculty.getId(), Faculty.class);
        Assertions.assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
    }


    @Test
    void testFindFacultiesByColor() {
        Faculty faculty1 = new Faculty();
        faculty1.setName("Test1");
        faculty1.setColor("Red");
        restTemplate.postForEntity(BASE_URL, faculty1, Faculty.class);

        Faculty faculty2 = new Faculty();
        faculty2.setName("Test2");
        faculty2.setColor("Blue");
        restTemplate.postForEntity(BASE_URL, faculty2, Faculty.class);

        // Запрашиваем факультеты по цвету
        ResponseEntity<Faculty[]> response = restTemplate.getForEntity(BASE_URL + "?color=Red", Faculty[].class);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(1, response.getBody().length);
        Assertions.assertEquals("Test1", response.getBody()[0].getName());
    }


    @Test
    public void testFindFacultiesByNameOrColor() {
        // Создаём факультет
        Faculty faculty = new Faculty();
        faculty.setName("Test");
        faculty.setColor("Purple");

        restTemplate.postForEntity(BASE_URL, faculty, Faculty.class);

        // Ищем по названию
        ResponseEntity<Faculty[]> response = restTemplate.getForEntity(BASE_URL + "/search?query=Test", Faculty[].class);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertTrue(response.getBody().length > 0);
    }

    @Test
    public void testGetStudentsByFacultyId() {
        Faculty faculty = new Faculty();
        faculty.setName("Test");
        faculty.setColor("Green");

        ResponseEntity<Faculty> createdResponse = restTemplate.postForEntity(BASE_URL, faculty, Faculty.class);
        Faculty createdFaculty = createdResponse.getBody();
        Assertions.assertNotNull(createdFaculty);
        Long facultyId = createdFaculty.getId();
        Assertions.assertNotNull(facultyId);

        Student student = new Student();
        student.setName("Student1");
        student.setAge(17);
        student.setFaculty(createdFaculty);

        ResponseEntity<Student> studentResponse = restTemplate.postForEntity("/students", student, Student.class);
        Assertions.assertEquals(HttpStatus.OK, studentResponse.getStatusCode());
        Assertions.assertNotNull(studentResponse.getBody());

        ResponseEntity<Student[]> response = restTemplate.getForEntity(
                BASE_URL + "/" + facultyId + "/students",
                Student[].class
        );

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertTrue(response.getBody().length > 0);
        Assertions.assertEquals("Student1", response.getBody()[0].getName());
    }
}