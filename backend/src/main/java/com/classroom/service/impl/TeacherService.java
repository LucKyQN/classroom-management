package com.classroom.service.impl;

import com.classroom.dto.request.GradeRequest;
import com.classroom.dto.response.GradeResponse;
import com.classroom.dto.response.UserResponse;
import com.classroom.entity.Grade;
import com.classroom.entity.User;
import com.classroom.exception.AppException;
import com.classroom.kafka.GradeNotificationEvent;
import com.classroom.kafka.GradeNotificationProducer;
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
public class TeacherService {

    private static final Logger log = LogManager.getLogger(TeacherService.class);

    private final UserRepository userRepository;
    private final GradeRepository gradeRepository;
    private final GradeNotificationProducer notificationProducer;

    public List<UserResponse> getStudentsInClass() {
        User teacher = getCurrentTeacher();
        if (teacher.getClassroom() == null) {
            throw new AppException("Giáo viên chưa được gán lớp");
        }

        return userRepository.findByClassroomIdAndRole(teacher.getClassroom().getId(), User.Role.STUDENT)
                .stream()
                .map(this::toUserResponse)
                .collect(Collectors.toList());
    }

    public GradeResponse upsertGrade(GradeRequest request) {
        User teacher = getCurrentTeacher();

        User student = userRepository.findById(request.getStudentId())
                .orElseThrow(() -> new AppException("Học sinh không tồn tại"));

        if (student.getRole() != User.Role.STUDENT) {
            throw new AppException("Người dùng không phải học sinh");
        }

        // Teacher can only grade students in their class
        if (teacher.getClassroom() != null && student.getClassroom() != null
                && !teacher.getClassroom().getId().equals(student.getClassroom().getId())) {
            throw new AppException("Học sinh không thuộc lớp của bạn");
        }

        Grade grade = gradeRepository.findByStudentIdAndSubject(student.getId(), request.getSubject())
                .orElse(Grade.builder().student(student).teacher(teacher).subject(request.getSubject()).build());

        grade.setScore(request.getScore());
        grade.setComment(request.getComment());
        grade.setTeacher(teacher);
        gradeRepository.save(grade);

        log.info("Teacher {} updated grade for student {} - subject: {} - score: {}",
                teacher.getEmail(), student.getEmail(), request.getSubject(), request.getScore());

        // Send Kafka notification
        notificationProducer.sendGradeNotification(GradeNotificationEvent.builder()
                .studentEmail(student.getEmail())
                .studentName(student.getFullName())
                .subject(request.getSubject())
                .score(request.getScore())
                .teacherName(teacher.getFullName())
                .build());

        return toGradeResponse(grade);
    }

    private User getCurrentTeacher() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException("Không tìm thấy người dùng"));
    }

    private UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .dateOfBirth(user.getDateOfBirth())
                .role(user.getRole().name())
                .classroomName(user.getClassroom() != null ? user.getClassroom().getName() : null)
                .build();
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
