package com.classroom.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GradeNotificationProducer {

    private static final Logger log = LogManager.getLogger(GradeNotificationProducer.class);
    private static final String TOPIC = "grade-notifications";

    private final KafkaTemplate<String, GradeNotificationEvent> kafkaTemplate;

    public void sendGradeNotification(GradeNotificationEvent event) {
        log.info("Sending grade notification for student: {}", event.getStudentEmail());
        kafkaTemplate.send(TOPIC, event.getStudentEmail(), event);
    }
}
