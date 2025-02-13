package com.toolScheduler.ToolSchedulerApplication.consumer;

import com.toolScheduler.ToolSchedulerApplication.model.ScanEvent;
import com.toolScheduler.ToolSchedulerApplication.model.ScanType;
import com.toolScheduler.ToolSchedulerApplication.model.Tenant;
import com.toolScheduler.ToolSchedulerApplication.model.UpdateEvent;
import com.toolScheduler.ToolSchedulerApplication.repository.TenantRepository;
import com.toolScheduler.ToolSchedulerApplication.service.GitHubAlertUpdateService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UpdateEventConsumer {

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
        System.out.println("Received UpdateEvent: " + event);

        // 1) Find credential for this (owner, repo)
        String tenantId = event.getTenantId();
        Tenant tenant = tenantRepository.findByTenantId(tenantId).orElse(null);
        if (tenant == null) {
            System.err.println("No tenant found for tenantId=" + tenantId);
            return;
        }

        // 2) Call GitHub to update the alert
        try {
            gitHubAlertUpdateService.updateAlert(event, tenant.getPat(), tenant.getOwner(), tenant.getRepo());
            System.out.println("Successfully updated alert in GitHub => " + event);

            // 3) Produce a single-tool-type ScanEvent to re-scan & sync changes
            ScanType st = event.getToolType();
            ScanEvent scanEvent = new ScanEvent(tenantId, List.of(st));
            scanEventProducer.send(scanTopic, scanEvent);
            System.out.println("Produced ScanEvent => " + scanEvent);

        } catch (Exception e) {
            System.err.println("Error updating alert in GitHub: " + e.getMessage());
            e.printStackTrace();
        }
    }
}