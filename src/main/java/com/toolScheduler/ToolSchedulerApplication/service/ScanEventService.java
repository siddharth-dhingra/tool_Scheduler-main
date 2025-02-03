package com.toolScheduler.ToolSchedulerApplication.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.toolScheduler.ToolSchedulerApplication.model.FileLocationEvent;
import com.toolScheduler.ToolSchedulerApplication.model.ScanEvent;
import com.toolScheduler.ToolSchedulerApplication.model.ScanType;
import com.toolScheduler.ToolSchedulerApplication.model.User;
import com.toolScheduler.ToolSchedulerApplication.producer.FileEventProducer;
import com.toolScheduler.ToolSchedulerApplication.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ScanEventService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScanEventService.class);

    private final UserRepository userRepository;
    private final GitHubScanService gitHubScanService;
    private final FileEventProducer fileEventProducer;

    public ScanEventService(UserRepository userRepository,
                            GitHubScanService gitHubScanService,
                            FileEventProducer fileEventProducer) {
        this.userRepository = userRepository;
        this.gitHubScanService = gitHubScanService;
        this.fileEventProducer = fileEventProducer;
    }

    public void processScanEvent(ScanEvent event) throws JsonMappingException, JsonProcessingException {
        User cred = findCredentials(event.getOwner(), event.getRepo());
        if (cred == null) {
            return;
        }

        List<ScanType> effectiveTypes = expandTypes(event.getTypes());

        for (ScanType toolType : effectiveTypes) {
            performScanAndPublish(cred, event, toolType);
        }
    }

    private User findCredentials(String owner, String repo) {
        User cred = userRepository.findByOwnerAndRepo(owner, repo);
        if (cred == null) {
            LOGGER.error("No credential found for {}/{}", owner, repo);
        }
        return cred;
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

    private void performScanAndPublish(User cred, ScanEvent event, ScanType toolType) throws JsonMappingException, JsonProcessingException {
        String rawJson = gitHubScanService.performSingleToolScan(cred.getPat(), event, toolType);

        String toolFolder = mapToolFolder(toolType);
        String folderPath = buildFolderPath(toolFolder, event);

        if (!createFolderIfNeeded(folderPath)) {
            LOGGER.error("Failed to create directories at: {}", folderPath);
            return;
        }

        String filePath = buildFilePath(folderPath, event);
        if (!writeJsonToFile(filePath, rawJson, toolType)) {
            return;
        }

        FileLocationEvent fle = new FileLocationEvent(filePath, event.getOwner(), event.getRepo(), toolFolder);
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

    private String buildFolderPath(String toolFolder, ScanEvent event) {
        return "/Users/siddharth.dhingra/Desktop/scan/"
                + toolFolder + "/"
                + event.getOwner() + "/"
                + event.getRepo();
    }

    private boolean createFolderIfNeeded(String folderPath) {
        File dir = new File(folderPath);
        if (!dir.exists()) {
            return dir.mkdirs();
        }
        return true;
    }

    private String buildFilePath(String folderPath, ScanEvent event) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String timestamp = LocalDateTime.now().format(formatter);
        String fileName = "scan_" + event.getOwner() + "_" + event.getRepo() + "_" + timestamp + ".json";
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