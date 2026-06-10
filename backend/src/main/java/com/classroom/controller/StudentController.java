package com.classroom.controller;

import com.classroom.dto.response.ApiResponse;
import com.classroom.dto.response.GradeResponse;
import com.classroom.service.impl.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
@PreAuthorize("hasRole('STUDENT')")
public class StudentController {

    private final StudentService studentService;

    @GetMapping("/grades")
    public ResponseEntity<ApiResponse<List<GradeResponse>>> getMyGrades() {
        return ResponseEntity.ok(ApiResponse.success(studentService.getMyGrades()));
    }
}
