package com.classroom.controller;

import com.classroom.dto.request.GradeRequest;
import com.classroom.dto.response.ApiResponse;
import com.classroom.dto.response.GradeResponse;
import com.classroom.dto.response.UserResponse;
import com.classroom.service.impl.TeacherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teacher")
@RequiredArgsConstructor
@PreAuthorize("hasRole('TEACHER')")
public class TeacherController {

    private final TeacherService teacherService;

    @GetMapping("/students")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getStudents() {
        return ResponseEntity.ok(ApiResponse.success(teacherService.getStudentsInClass()));
    }

    @PostMapping("/grades")
    public ResponseEntity<ApiResponse<GradeResponse>> upsertGrade(@Valid @RequestBody GradeRequest request) {
        GradeResponse response = teacherService.upsertGrade(request);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật điểm thành công", response));
    }
}
