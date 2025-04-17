package ru.hogwarts.school.webMvcTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import ru.hogwarts.school.controller.StudentController;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;




@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = {StudentController.class})
public class StudentControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private StudentService studentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetStudentById() throws Exception {
        Student student = new Student();
        student.setId(1L);
        student.setName("John");
        student.setAge(20);

        when(studentService.findStudent(1L)).thenReturn(student);

        mockMvc.perform(get("/student/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.age").value(20));
    }

    @Test
    public void testCreateStudent() throws Exception {
        Student student = new Student();
        student.setId(1L);
        student.setName("John");
        student.setAge(20);

        when(studentService.addStudent(any(Student.class))).thenReturn(student);

        mockMvc.perform(post("/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(student)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John"));
    }

    @Test
    public void testEditStudent() throws Exception {
        Student student = new Student();
        student.setId(1L);
        student.setName("UpdatedName");
        student.setAge(22);

        when(studentService.editStudent(eq(1L), any(Student.class))).thenReturn(student);

        mockMvc.perform(put("/student/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(student)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("UpdatedName"))
                .andExpect(jsonPath("$.age").value(22));
    }

    @Test
    public void testDeleteStudent() throws Exception {
        doNothing().when(studentService).deleteStudent(1L);

        mockMvc.perform(delete("/student/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void testFindStudentsByAge() throws Exception {
        Student student = new Student();
        student.setId(1L);
        student.setName("John");
        student.setAge(18);

        when(studentService.findByAge(18)).thenReturn(List.of(student));

        mockMvc.perform(get("/student").param("age", "18"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("John"))
                .andExpect(jsonPath("$[0].age").value(18));
    }

    @Test
    public void testFindStudentsByAgeRange() throws Exception {
        Student student = new Student();
        student.setId(1L);
        student.setName("Alice");
        student.setAge(20);

        when(studentService.findStudentsByAgeRange(18, 25)).thenReturn(List.of(student));

        mockMvc.perform(get("/student/by-age").param("min", "18").param("max", "25"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Alice"))
                .andExpect(jsonPath("$[0].age").value(20));
    }


    @Test
    public void testFindStudentFaculty() throws Exception {
        Faculty faculty = new Faculty();
        faculty.setId(1L);
        faculty.setName("Engineering");
        faculty.setColor("Blue");

        when(studentService.getFacultyByStudentId(1L)).thenReturn(faculty);

        mockMvc.perform(get("/student/1/faculty"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Engineering"))
                .andExpect(jsonPath("$.color").value("Blue"));
    }

    @Test
    public void testPrintStudentsParallel() throws Exception {
        // Настраиваем мок
        doNothing().when(studentService).printStudentsParallel();

        mockMvc.perform(get("/student/print-parallel"))
                .andExpect(status().isOk())
                .andExpect(content().string("Printing students in parallel mode"));
    }

    @Test
    public void testPrintStudentsSynchronized() throws Exception {
        // Настраиваем мок
        doNothing().when(studentService).printStudentsSynchronized();

        mockMvc.perform(get("/student/print-synchronized"))
                .andExpect(status().isOk())
                .andExpect(content().string("Printing students in synchronized mode"));
    }
}



