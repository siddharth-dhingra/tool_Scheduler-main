package com.toolScheduler.ToolSchedulerApplication.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.toolScheduler.ToolSchedulerApplication.model.FileLocationEvent;
import com.toolScheduler.ToolSchedulerApplication.model.ScanEvent;
import com.toolScheduler.ToolSchedulerApplication.model.ScanType;
import com.toolScheduler.ToolSchedulerApplication.model.Tenant;
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
import java.util.List;
import java.util.Optional;

@Service
public class ScanEventService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScanEventService.class);

    private final TenantRepository tenantRepository;
    private final GitHubScanService gitHubScanService;
    private final FileEventProducer fileEventProducer;

    public ScanEventService(TenantRepository tenantRepository,
                            GitHubScanService gitHubScanService,
                            FileEventProducer fileEventProducer) {
        this.tenantRepository = tenantRepository;
        this.gitHubScanService = gitHubScanService;
        this.fileEventProducer = fileEventProducer;
    }

    public void processScanEvent(ScanEvent event) throws JsonMappingException, JsonProcessingException {
        String tenantId = event.getTenantId();
        Optional<Tenant> optTenant = tenantRepository.findByTenantId(tenantId);
        if (optTenant.isEmpty()) {
            LOGGER.error("No tenant found with tenantId={}", tenantId);
            return;
        }
        Tenant tenant = optTenant.get();

        List<ScanType> effectiveTypes = expandTypes(event.getTypes());

        for (ScanType toolType : effectiveTypes) {
            performScanAndPublish(tenant, toolType);
        }
    }

    private List<ScanType> expandTypes(List<ScanType> incomingTypes) {
        if (incomingTypes == null || incomingTypes.isEmpty()) {
            return List.of();
        }
        if (incomingTypes.contains(ScanType.ALL)) {
            return List.of(ScanType.CODESCAN, ScanType.DEPENDABOT, ScanType.SECRETSCAN);
        }
        return incomingTypes;
    }

    private void performScanAndPublish(Tenant tenant, ScanType toolType) throws JsonMappingException, JsonProcessingException {
        String rawJson = gitHubScanService.performSingleToolScan(
                tenant.getPat(), 
                tenant.getOwner(), 
                tenant.getRepo(), 
                toolType
        );

        String toolFolder = mapToolFolder(toolType);
        String folderPath = buildFolderPath(toolFolder, tenant.getOwner(), tenant.getRepo());

        if (!createFolderIfNeeded(folderPath)) {
            LOGGER.error("Failed to create directories at: {}", folderPath);
            return;
        }

        String filePath = buildFilePath(folderPath, tenant.getOwner(), tenant.getRepo());
        if (!writeJsonToFile(filePath, rawJson, toolType)) {
            return;
        }

        FileLocationEvent fle = new FileLocationEvent(filePath, tenant.getEsIndex(), toolFolder);
        fileEventProducer.publishFileLocationEvent(fle);
        LOGGER.info("Published FileLocationEvent => [filePath={}, toolName={}]", filePath, toolFolder);
    }

    private String mapToolFolder(ScanType t) {
        return switch (t) {
            case CODESCAN -> "codescan";
            case DEPENDABOT -> "dependabot";
            case SECRETSCAN -> "secretscan";
            default -> "mixed";
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

    private boolean writeJsonToFile(String filePath, String rawJson, ScanType toolType) {
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