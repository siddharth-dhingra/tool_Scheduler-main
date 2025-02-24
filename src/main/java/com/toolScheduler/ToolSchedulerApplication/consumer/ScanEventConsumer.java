package com.toolScheduler.ToolSchedulerApplication.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.toolScheduler.ToolSchedulerApplication.dto.ScanRequestEvent;
import com.toolScheduler.ToolSchedulerApplication.model.ScanEvent;
import com.toolScheduler.ToolSchedulerApplication.service.ScanEventService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ScanEventConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScanEventConsumer.class);

    private final ScanEventService scanEventService;
    private final ObjectMapper objectMapper;

    public ScanEventConsumer(ScanEventService scanEventService, ObjectMapper objectMapper) {
        this.scanEventService = scanEventService;
        this.objectMapper = objectMapper;
    }

    @Value("${app.kafka.topics.scan}")
    private String scanTopic;

    @KafkaListener(topics = "${app.kafka.topics.scan}", groupId = "${spring.kafka.consumer.group-id}", containerFactory = "stringListenerContainerFactory")
    public void consumeScanEvent(String message)
            throws JsonMappingException, JsonProcessingException {

        try {
            ScanRequestEvent eventWrapper = objectMapper.readValue(message, ScanRequestEvent.class);
            ScanEvent event = eventWrapper.getPayload();
            String jobId = event.getJobId();
            
            LOGGER.info("Received ScanRequestEvent of type {} with payload: {}", eventWrapper.getType(), event);
            
            scanEventService.processScanEvent(event, jobId);
        } catch (Exception e) {
            LOGGER.error("Error processing scan event message: {}", message, e);
        }
    }
}

