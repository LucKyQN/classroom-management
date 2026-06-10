package com.classroom.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class GradeRequest {
    @NotNull(message = "Student ID không được để trống")
    private Long studentId;

    @NotBlank(message = "Môn học không được để trống")
    private String subject;

    @NotNull
    @DecimalMin(value = "0.0", message = "Điểm không được nhỏ hơn 0")
    @DecimalMax(value = "10.0", message = "Điểm không được lớn hơn 10")
    private Double score;

    private String comment;
}
