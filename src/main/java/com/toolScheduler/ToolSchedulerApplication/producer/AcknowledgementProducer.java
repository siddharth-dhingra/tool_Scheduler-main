package com.toolScheduler.ToolSchedulerApplication.producer;

import com.toolScheduler.ToolSchedulerApplication.dto.PullAcknowledgement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class AcknowledgementProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(AcknowledgementProducer.class);

    @Value("${app.kafka.topics.job-acknowledgement}")
    private String ackTopic;

    private final KafkaTemplate<String, PullAcknowledgement> kafkaTemplate;

    public AcknowledgementProducer(KafkaTemplate<String, PullAcknowledgement> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendAcknowledgement(PullAcknowledgement ack) {
        kafkaTemplate.send(ackTopic, ack);
        LOGGER.info("Published PullAcknowledgement to topic {} => {}", ackTopic, ack);
    }
}