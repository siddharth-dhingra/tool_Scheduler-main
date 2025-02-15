package com.toolScheduler.ToolSchedulerApplication.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.toolScheduler.ToolSchedulerApplication.dto.ScanRequestEvent;
import com.toolScheduler.ToolSchedulerApplication.model.ScanEvent;
import com.toolScheduler.ToolSchedulerApplication.service.ScanEventService;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ScanEventConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScanEventConsumer.class);

    private final ScanEventService scanEventService;

    public ScanEventConsumer(ScanEventService scanEventService) {
        this.scanEventService = scanEventService;
    }

    @Value("${app.kafka.topics.scan}")
    private String scanTopic;

    @KafkaListener(topics = "${app.kafka.topics.scan}", groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "scanEventListenerContainerFactory")
    public void consumeScanEvent(ConsumerRecord<String, ScanRequestEvent> record)
            throws JsonMappingException, JsonProcessingException {

        ScanRequestEvent eventWrapper = record.value();
        String eventId = eventWrapper.getEventId();
        ScanEvent event = eventWrapper.getPayload();
        LOGGER.info("Received ScanRequestEvent of type {} with payload: {}", eventWrapper.getType(), event);
        scanEventService.processScanEvent(event, eventId);
    }
}

