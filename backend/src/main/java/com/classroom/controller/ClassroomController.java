package com.classroom.controller;

import com.classroom.dto.response.ApiResponse;
import com.classroom.entity.Classroom;
import com.classroom.repository.ClassroomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/classrooms")
@RequiredArgsConstructor
public class ClassroomController {

    private final ClassroomRepository classroomRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Classroom>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(classroomRepository.findAll()));
    }
}
