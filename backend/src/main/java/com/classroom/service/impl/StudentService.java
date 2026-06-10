package com.classroom.service.impl;

import com.classroom.dto.response.GradeResponse;
import com.classroom.entity.Grade;
import com.classroom.exception.AppException;
import com.classroom.repository.GradeRepository;
import com.classroom.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentService {

    private static final Logger log = LogManager.getLogger(StudentService.class);

    private final GradeRepository gradeRepository;
    private final UserRepository userRepository;

    public List<GradeResponse> getMyGrades() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        var student = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException("Không tìm thấy người dùng"));

        log.info("Student {} fetching their grades", email);

        return gradeRepository.findByStudentId(student.getId())
                .stream().map(this::toGradeResponse).collect(Collectors.toList());
    }

    private GradeResponse toGradeResponse(Grade grade) {
        return GradeResponse.builder()
                .id(grade.getId())
                .subject(grade.getSubject())
                .score(grade.getScore())
                .comment(grade.getComment())
                .teacherName(grade.getTeacher().getFullName())
                .updatedAt(grade.getUpdatedAt())
                .build();
    }
}
