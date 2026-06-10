package com.classroom.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class GradeResponse {
    private Long id;
    private String subject;
    private Double score;
    private String comment;
    private String teacherName;
    private LocalDateTime updatedAt;
}
