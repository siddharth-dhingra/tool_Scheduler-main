package com.toolScheduler.ToolSchedulerApplication.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.toolScheduler.ToolSchedulerApplication.dto.ScanParseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class FileEventProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileEventProducer.class);
    private final KafkaTemplate<String, String> fileLocationProducer;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${app.kafka.topics.filelocation}")
    private String fileLocationTopic;

    @Value("${app.kafka.topics.parse-destination}")
    private String parseDestinationTopic;

    public FileEventProducer(KafkaTemplate<String, String> fileLocationProducer) {
        this.fileLocationProducer = fileLocationProducer;
    }

    public void publishFileLocationEvent(ScanParseEvent event) {
        try {
            event.getPayload().setDestinationTopic(parseDestinationTopic);
            // Convert the event to a JSON string.
            String json = objectMapper.writeValueAsString(event);
            fileLocationProducer.send(fileLocationTopic, json).whenComplete((result, exception) -> {
                if (exception != null) {
                    LOGGER.error("Failed to send ScanParseEvent: " + exception.getMessage());
                } else {
                    var metadata = result.getRecordMetadata();
                    LOGGER.info("ScanParseEvent sent to topic=" + metadata.topic() +
                            " partition=" + metadata.partition() +
                            " offset=" + metadata.offset());
                }
            });
            LOGGER.info("Published ScanParseEvent to topic {} => {}", fileLocationTopic, json);
        } catch (Exception e) {
            LOGGER.error("Error serializing ScanParseEvent", e);
        }
    }
}
