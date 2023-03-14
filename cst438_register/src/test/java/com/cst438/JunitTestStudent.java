package com.cst438;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.cst438.controller.StudentController;
import com.cst438.domain.Student;
import com.cst438.domain.StudentDTO;
import com.cst438.domain.StudentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@ContextConfiguration(classes = { StudentController.class })
// @AutoConfigureMockMvc(addFilters = false)
@WebMvcTest
public class JunitTestStudent {
  @Autowired
  private MockMvc mvc;
  @MockBean
  private StudentRepository studentRepo;
  // @MockBean
  // private ObjectMapper mapper;

  @Test
  public void getStudent() throws Exception {

    Student s1 = new Student();
    String name = "JohnnyTest";
    String email = "test@email.com";
    int id = 20;
    int statusCode = 5;
    String status = "Contact IT FOR ASSITANCE";
    s1.setName(name);
    s1.setEmail(email);
    s1.setStudent_id(id);
    s1.setStatusCode(statusCode);
    s1.setStatus(status);
    given(studentRepo.findById(id)).willReturn(Optional.of(s1));
    mvc.perform(MockMvcRequestBuilders.get("/student/" + id)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.name", is(name)))
        .andExpect(jsonPath("$.email", is(email)))
        .andExpect(jsonPath("$.status", is(status)))
        .andExpect(jsonPath("$.statusCode", is(statusCode)))
        ;
  }

  @Test
  public void createStudent() throws Exception {
    Student s1 = new Student();
    String name = "JohnnyTest";
    String email = "test@email.com";
    int id = 20;
    s1.setName(name);
    s1.setEmail(email);
    s1.setStudent_id(id);
    StudentDTO studDto = new StudentDTO();
    studDto.setName(name);
    studDto.setEmail(email);
    given(studentRepo.findByEmail(email)).willReturn(null);
    given(studentRepo.save(Mockito.any(Student.class))).willReturn(s1);
    // given(studentRepo.save(s1)).willReturn(s1);
    MockHttpServletResponse response = mvc.perform(
        MockMvcRequestBuilders
            .post("/student")
            .content(new ObjectMapper().writeValueAsString(studDto))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.name", is(name)))
        .andExpect(jsonPath("$.email", is(email)))
        .andReturn().getResponse();
    System.out.println("ContentType");
    System.out.println(response.getContentType());

  }

  @Test
  public void createStudentAlreadyExists() throws Exception {
    Student s1 = new Student();
    String name = "JohnnyTest";
    String email = "test@email.com";
    s1.setName(name);
    s1.setEmail(email);
    StudentDTO studDto = new StudentDTO();
    studDto.setName(name);
    studDto.setEmail(email);

    // Returns student S1 Indicating the student with given email already exists
    given(studentRepo.findByEmail(email)).willReturn(s1);
    MockHttpServletResponse response = mvc.perform(
        MockMvcRequestBuilders
            .post("/student")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(studDto))
            .accept(MediaType.APPLICATION_JSON))
        // Expect request to bounce & throw exception Bad request.
        .andExpect(status().isBadRequest())
        .andReturn().getResponse();
    System.out.println("ContentType");
    System.out.println(response.getContentType());
  }

  @Test
  public void updateStudentHold() throws Exception {

    Student s1 = new Student();
    String name = "JohnnyTest";
    String email = "test@email.com";
    int studentId = 30;
    int oldStatusCode = 0;
    String oldStatus = "MuyBueno!";

    StudentDTO studDto = new StudentDTO();
    Integer newCode = 9;
    String newStatus = "Overdue Fees";

    s1.setStudent_id(studentId);
    s1.setName(name);
    s1.setEmail(email);
    s1.setStatusCode(oldStatusCode);
    s1.setStatus(oldStatus);
    studDto.setStatusCode(9);
    studDto.setStatus("Overdue Fees");
    given(studentRepo.findById(studentId)).willReturn(Optional.of(s1));
    given(studentRepo.save(s1)).willReturn(s1);

    MockHttpServletResponse response = mvc.perform(
        MockMvcRequestBuilders
            .put("/student/" + studentId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(studDto))
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.name", is(name)))
        .andExpect(jsonPath("$.email", is(email)))
        .andExpect(jsonPath("$.status", is(newStatus)))
        .andExpect(jsonPath("$.statusCode", is(newCode)))
        .andExpect(jsonPath("$.status", not(oldStatus)))
        .andExpect(jsonPath("$.statusCode", not(oldStatusCode)))
        .andReturn().getResponse();
    System.out.println("ContentType");
    System.out.println(response.getContentType());
  }
}
