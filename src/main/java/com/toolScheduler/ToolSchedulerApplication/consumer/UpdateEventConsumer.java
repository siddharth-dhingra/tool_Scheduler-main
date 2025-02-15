package com.toolScheduler.ToolSchedulerApplication.consumer;

import com.toolScheduler.ToolSchedulerApplication.model.ScanEvent;
import com.toolScheduler.ToolSchedulerApplication.model.ToolType;
import com.toolScheduler.ToolSchedulerApplication.model.Tenant;
import com.toolScheduler.ToolSchedulerApplication.model.UpdateEvent;
import com.toolScheduler.ToolSchedulerApplication.repository.TenantRepository;
import com.toolScheduler.ToolSchedulerApplication.service.GitHubAlertUpdateService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class UpdateEventConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateEventConsumer.class);

    @Value("${app.kafka.topics.update}")
    private String updateTopic;

    @Value("${app.kafka.topics.scan}")
    private String scanTopic;

    private final TenantRepository tenantRepository;
    private final GitHubAlertUpdateService gitHubAlertUpdateService;
    private final KafkaTemplate<String, ScanEvent> scanEventProducer;

    public UpdateEventConsumer(TenantRepository tenantRepository,
                               GitHubAlertUpdateService gitHubAlertUpdateService,
                               KafkaTemplate<String, ScanEvent> scanEventProducer) {
        this.tenantRepository = tenantRepository;
        this.gitHubAlertUpdateService = gitHubAlertUpdateService;
        this.scanEventProducer = scanEventProducer;
    }

    @KafkaListener(
        topics = "${app.kafka.topics.update}", 
        groupId = "toolscheduler-group-update",  
        containerFactory = "updateEventListenerContainerFactory"
    )
    public void consumeUpdateEvent(ConsumerRecord<String, UpdateEvent> record) {
        UpdateEvent event = record.value();
        LOGGER.info("Received UpdateEvent: " + event);

        // 1) Find credential for this (owner, repo)
        String tenantId = event.getTenantId();
        Tenant tenant = tenantRepository.findByTenantId(tenantId).orElse(null);
        if (tenant == null) {
            LOGGER.error("No tenant found for tenantId=" + tenantId);
            return;
        }

        // 2) Call GitHub to update the alert
        try {
            gitHubAlertUpdateService.updateAlert(event, tenant.getPat(), tenant.getOwner(), tenant.getRepo());
            LOGGER.info("Successfully updated alert in GitHub => " + event);

            // 3) Produce a single-tool-type ScanEvent to re-scan & sync changes
            ToolType st = event.getToolType();
            ScanEvent scanEvent = new ScanEvent(tenantId, st);
            scanEventProducer.send(scanTopic, scanEvent);
            LOGGER.info("Produced ScanEvent => " + scanEvent);

        } catch (Exception e) {
            LOGGER.error("Error updating alert in GitHub: " + e.getMessage());
            e.printStackTrace();
        }
    }
}