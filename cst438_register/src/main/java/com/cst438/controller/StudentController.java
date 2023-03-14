package com.cst438.controller;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.cst438.domain.Student;
import com.cst438.domain.StudentDTO;
import com.cst438.domain.StudentRepository;

@RestController
public class StudentController {
  private final StudentRepository studentRepo;

  public StudentController(StudentRepository studentRepo) {
    this.studentRepo = studentRepo;
  }

  @GetMapping("/student/{id}")
  public StudentDTO getStudent(@PathVariable int id) {
    Student student = studentRepo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
            "Student Not Found with ID: " + id));
    return studentToDTO(student);
  }

  @PutMapping("/student/{id}")
  public StudentDTO modifyStudentHold(@PathVariable int id, @RequestBody StudentDTO student) {
    Student s = studentRepo
        .findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
            "Student With ID: " + id + " not found."));
    s.setStatusCode(student.getStatusCode());
    s.setStatus(student.getStatus());
    return studentToDTO(studentRepo.save(s));
  }

  @PostMapping("/student")
  public StudentDTO addStudent(@RequestBody StudentDTO student) {
    if (!StringUtils.hasText(student.getEmail()) && !StringUtils.hasText(student.getName()))
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "Request Must Contain both Email, and name");

    Student s = studentRepo.findByEmail(student.getEmail());
    if (s != null)
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "Student Already Exists with Email: " + student.getEmail());

    s = new Student();
    s.setName(student.getName());
    s.setEmail(student.getEmail());
    return studentToDTO(studentRepo.save(s));

  }

  public StudentDTO studentToDTO(Student s) {
    StudentDTO convertedDTO = new StudentDTO();
    convertedDTO.setId(s.getStudent_id());
    convertedDTO.setName(s.getName());
    convertedDTO.setEmail(s.getEmail());
    convertedDTO.setStatusCode(s.getStatusCode());
    convertedDTO.setStatus(s.getStatus());
    return convertedDTO;
  }

}
