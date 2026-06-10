package com.classroom.kafka;

import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class GradeNotificationEvent {
    private String studentEmail;
    private String studentName;
    private String subject;
    private Double score;
    private String teacherName;
}
