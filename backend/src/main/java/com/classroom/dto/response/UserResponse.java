package com.classroom.dto.response;

import lombok.*;
import java.time.LocalDate;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class UserResponse {
    private Long id;
    private String email;
    private String fullName;
    private LocalDate dateOfBirth;
    private String role;
    private String classroomName;
}
