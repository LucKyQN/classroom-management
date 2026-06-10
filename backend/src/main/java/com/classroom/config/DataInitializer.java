package com.classroom.config;

import com.classroom.entity.Classroom;
import com.classroom.repository.ClassroomRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LogManager.getLogger(DataInitializer.class);
    private final ClassroomRepository classroomRepository;

    @Override
    public void run(String... args) {
        if (classroomRepository.count() == 0) {
            classroomRepository.saveAll(List.of(
                    Classroom.builder().name("DHKTMT17A").description("Lớp Khoa học Máy tính 17A").build(),
                    Classroom.builder().name("DHKTMT17B").description("Lớp Khoa học Máy tính 17B").build(),
                    Classroom.builder().name("DHKTMT18A").description("Lớp Khoa học Máy tính 18A").build(),
                    Classroom.builder().name("DHKTMT18B").description("Lớp Khoa học Máy tính 18B").build()
            ));
            log.info("Seeded initial classrooms");
        }
    }
}
