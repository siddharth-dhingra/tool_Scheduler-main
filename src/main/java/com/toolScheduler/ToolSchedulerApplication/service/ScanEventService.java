package com.toolScheduler.ToolSchedulerApplication.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.toolScheduler.ToolSchedulerApplication.dto.PullAcknowledgement;
import com.toolScheduler.ToolSchedulerApplication.dto.ScanParseEvent;
import com.toolScheduler.ToolSchedulerApplication.model.AcknowledgementEvent;
import com.toolScheduler.ToolSchedulerApplication.model.AcknowledgementStatus;
import com.toolScheduler.ToolSchedulerApplication.model.FileLocationEvent;
import com.toolScheduler.ToolSchedulerApplication.model.ScanEvent;
import com.toolScheduler.ToolSchedulerApplication.model.ToolType;
import com.toolScheduler.ToolSchedulerApplication.model.Tenant;
import com.toolScheduler.ToolSchedulerApplication.producer.AcknowledgementProducer;
import com.toolScheduler.ToolSchedulerApplication.producer.FileEventProducer;
import com.toolScheduler.ToolSchedulerApplication.repository.TenantRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class ScanEventService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScanEventService.class);

    private final TenantRepository tenantRepository;
    private final GitHubScanService gitHubScanService;
    private final FileEventProducer fileEventProducer;
    private final AcknowledgementProducer acknowledgementProducer;

    public ScanEventService(TenantRepository tenantRepository,
                            GitHubScanService gitHubScanService,
                            FileEventProducer fileEventProducer,
                            AcknowledgementProducer acknowledgementProducer) {
        this.tenantRepository = tenantRepository;
        this.gitHubScanService = gitHubScanService;
        this.fileEventProducer = fileEventProducer;
        this.acknowledgementProducer = acknowledgementProducer;
    }

    public void processScanEvent(ScanEvent event, String eventId) throws JsonMappingException, JsonProcessingException {
        String tenantId = event.getTenantId();
        Optional<Tenant> optTenant = tenantRepository.findByTenantId(tenantId);
        if (optTenant.isEmpty()) {
            LOGGER.error("No tenant found with tenantId={}", tenantId);
            return;
        }
        Tenant tenant = optTenant.get();

        if(event.getToolType() == null){
            return;
        }
        ToolType effectiveTypes = event.getToolType();

        performScanAndPublish(tenant, effectiveTypes, eventId);
    }

    private void performScanAndPublish(Tenant tenant, ToolType toolType, String eventId) throws JsonMappingException, JsonProcessingException {
        String rawJson = gitHubScanService.performSingleToolScan(
                tenant.getPat(), 
                tenant.getOwner(), 
                tenant.getRepo(), 
                toolType
        );

        ToolType toolFolder = mapToolFolder(toolType);
        String folderPath = buildFolderPath(toolFolder.toString(), tenant.getOwner(), tenant.getRepo());

        if (!createFolderIfNeeded(folderPath)) {
            LOGGER.error("Failed to create directories at: {}", folderPath);
            return;
        }

        String filePath = buildFilePath(folderPath, tenant.getOwner(), tenant.getRepo());
        if (!writeJsonToFile(filePath, rawJson, toolType)) {
            return;
        }

        String jobId = eventId;
        AcknowledgementEvent ackEvent = new AcknowledgementEvent(jobId);
        // Optionally, set status based on your processing logic:
        ackEvent.setStatus(AcknowledgementStatus.SUCCESS);
        PullAcknowledgement ack = new PullAcknowledgement(null, ackEvent);
        acknowledgementProducer.sendAcknowledgement(ack);
        LOGGER.info("Published PullAcknowledgement with jobId={}", jobId);

        FileLocationEvent fle = new FileLocationEvent(tenant.getTenantId(),filePath, toolFolder);
        ScanParseEvent scanParseEvent = new ScanParseEvent(null,fle);
        fileEventProducer.publishFileLocationEvent(scanParseEvent);
        LOGGER.info("Published FileLocationEvent => [filePath={}, toolName={}]", filePath, toolFolder);
    }

    private ToolType mapToolFolder(ToolType t) {
        return switch (t) {
            case CODESCAN -> ToolType.CODESCAN;
            case DEPENDABOT -> ToolType.DEPENDABOT;
            case SECRETSCAN -> ToolType.SECRETSCAN;
            default -> ToolType.CODESCAN;
        };
    }

    private String buildFolderPath(String toolFolder, String owner, String repo) {
        return "/Users/siddharth.dhingra/Desktop/scan/"
                + toolFolder + "/"
                + owner + "/"
                + repo;
    }

    private boolean createFolderIfNeeded(String folderPath) {
        File dir = new File(folderPath);
        if (!dir.exists()) {
            return dir.mkdirs();
        }
        return true;
    }

    private String buildFilePath(String folderPath, String owner, String repo) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String timestamp = LocalDateTime.now().format(formatter);
        String fileName = "scan_" + owner + "_" + repo + "_" + timestamp + ".json";
        return folderPath + File.separator + fileName;
    }

    private boolean writeJsonToFile(String filePath, String rawJson, ToolType toolType) {
        try (FileWriter fw = new FileWriter(filePath)) {
            fw.write(rawJson);
            LOGGER.info("Wrote scan data for tool={} to {}", toolType, filePath);
            return true;
        } catch (IOException e) {
            LOGGER.error("Error writing JSON to file at path={}", filePath, e);
            return false;
        }
    }
}