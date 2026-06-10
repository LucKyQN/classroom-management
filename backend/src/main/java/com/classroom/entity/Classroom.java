package com.classroom.entity;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

@Entity
@Table(name = "classrooms")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class Classroom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;
    @JsonIgnore  // thêm dòng này

    @OneToMany(mappedBy = "classroom", fetch = FetchType.LAZY)
    private List<User> students;
}
