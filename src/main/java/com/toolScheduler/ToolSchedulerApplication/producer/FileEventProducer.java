package com.toolScheduler.ToolSchedulerApplication.producer;

import com.toolScheduler.ToolSchedulerApplication.dto.ScanParseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class FileEventProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileEventProducer.class);

    private final KafkaTemplate<String, ScanParseEvent> fileLocationProducer;

    @Value("${app.kafka.topics.filelocation}")
    private String fileLocationTopic;

    public FileEventProducer(KafkaTemplate<String, ScanParseEvent> fileLocationProducer) {
        this.fileLocationProducer = fileLocationProducer;
    }

    public void publishFileLocationEvent(ScanParseEvent event) {
        fileLocationProducer.send(fileLocationTopic, event).whenComplete((result, exception) -> {
            if (exception != null) {
                LOGGER.error("Failed to send ParseRequestEvent: " + exception.getMessage());
            } else {
                var metadata = result.getRecordMetadata();
                LOGGER.info("ParseRequestEvent sent to topic=" + metadata.topic() +
                        " partition=" + metadata.partition() +
                        " offset=" + metadata.offset());
            }
        });
        LOGGER.info("Published FileLocationEvent to topic {} => {}", fileLocationTopic, event);
    }
}
