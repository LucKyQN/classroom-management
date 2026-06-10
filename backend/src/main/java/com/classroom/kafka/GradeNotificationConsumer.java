package com.classroom.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GradeNotificationConsumer {

    private static final Logger log = LogManager.getLogger(GradeNotificationConsumer.class);

    private final JavaMailSender mailSender;

    @KafkaListener(topics = "grade-notifications", groupId = "classroom-group")
    public void handleGradeNotification(GradeNotificationEvent event) {
        log.info("Received grade notification for: {}", event.getStudentEmail());
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(event.getStudentEmail());
            message.setSubject("Cập nhật điểm số - Môn: " + event.getSubject());
            message.setText(String.format(
                    "Xin chào %s,\n\nGiáo viên %s vừa cập nhật điểm của bạn:\n" +
                    "Môn học: %s\nĐiểm: %.1f/10\n\nTrân trọng,\nHệ thống Quản lý Lớp học",
                    event.getStudentName(), event.getTeacherName(),
                    event.getSubject(), event.getScore()
            ));
            mailSender.send(message);
            log.info("Email sent to: {}", event.getStudentEmail());
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", event.getStudentEmail(), e.getMessage());
        }
    }
}
