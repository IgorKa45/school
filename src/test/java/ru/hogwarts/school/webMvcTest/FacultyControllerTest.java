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
import ru.hogwarts.school.controller.FacultyController;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.FacultyService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = {FacultyController.class})
public class FacultyControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private FacultyService facultyService;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetFacultyById() throws Exception {
        Faculty faculty = new Faculty();
        faculty.setId(1L);
        faculty.setName("Engineering");
        faculty.setColor("Blue");

        when(facultyService.findFaculty(1L)).thenReturn(faculty);

        mockMvc.perform(get("/faculty/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Engineering"))
                .andExpect(jsonPath("$.color").value("Blue"));
    }

    @Test
    public void testFindFacultiesByColor() throws Exception {
        Faculty faculty = new Faculty();
        faculty.setId(1L);
        faculty.setName("Engineering");
        faculty.setColor("Blue");

        List<Faculty> faculties = Collections.singletonList(faculty);
        when(facultyService.findByColor("Blue")).thenReturn(faculties);

        mockMvc.perform(get("/faculty").param("color", "Blue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Engineering"))
                .andExpect(jsonPath("$[0].color").value("Blue"));
    }


    @Test
    public void testCreateFaculty() throws Exception {
        Faculty faculty = new Faculty();
        faculty.setId(1L);
        faculty.setName("Engineering");
        faculty.setColor("Blue");

        when(facultyService.addFaculty(any(Faculty.class))).thenReturn(faculty);

        mockMvc.perform(post("/faculty")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(faculty)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Engineering"))
                .andExpect(jsonPath("$.color").value("Blue"));
    }

    @Test
    public void testEditFaculty() throws Exception {
        Faculty faculty = new Faculty();
        faculty.setId(1L);
        faculty.setName("Updated Faculty");
        faculty.setColor("Red");

        when(facultyService.editFaculty(eq(1L), any(Faculty.class))).thenReturn(faculty);

        mockMvc.perform(put("/faculty/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(faculty)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Faculty"))
                .andExpect(jsonPath("$.color").value("Red"));
    }

    @Test
    public void testDeleteFaculty() throws Exception {
        doNothing().when(facultyService).deleteFaculty(1L);

        mockMvc.perform(delete("/faculty/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetStudentsByFacultyId() throws Exception {
        Student student = new Student();
        student.setId(1L);
        student.setName("John");
        student.setAge(20);

        when(facultyService.getStudentsByFacultyId(1L)).thenReturn(List.of(student));

        mockMvc.perform(get("/faculty/1/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("John"))
                .andExpect(jsonPath("$[0].age").value(20));
    }

    @Test
    public void testFindFacultiesByNameOrColor() throws Exception {
        Faculty faculty = new Faculty();
        faculty.setId(1L);
        faculty.setName("Engineering");
        faculty.setColor("Blue");

        when(facultyService.findFacultiesByNameOrColor("Engineering")).thenReturn(List.of(faculty));

        mockMvc.perform(get("/faculty/search").param("query", "Engineering"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Engineering"))
                .andExpect(jsonPath("$[0].color").value("Blue"));
    }



}
