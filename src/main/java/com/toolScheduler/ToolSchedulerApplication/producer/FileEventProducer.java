package com.toolScheduler.ToolSchedulerApplication.producer;

import com.toolScheduler.ToolSchedulerApplication.model.FileLocationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class FileEventProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileEventProducer.class);

    private final KafkaTemplate<String, FileLocationEvent> fileLocationProducer;

    @Value("${app.kafka.topics.filelocation}")
    private String fileLocationTopic;

    public FileEventProducer(KafkaTemplate<String, FileLocationEvent> fileLocationProducer) {
        this.fileLocationProducer = fileLocationProducer;
    }

    public void publishFileLocationEvent(FileLocationEvent event) {
        fileLocationProducer.send(fileLocationTopic, event);
        LOGGER.info("Published FileLocationEvent to topic {} => {}", fileLocationTopic, event);
    }
}
