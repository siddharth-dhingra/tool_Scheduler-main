package com.toolScheduler.ToolSchedulerApplication.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.toolScheduler.ToolSchedulerApplication.model.ToolType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class GitHubScanService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GitHubScanService.class);

    private final WebClient.Builder webClientBuilder;

    public GitHubScanService(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    public String performSingleToolScan(String pat,  String owner, String repo, ToolType toolType) throws JsonMappingException, JsonProcessingException {
        switch (toolType) {
            case CODESCAN:
                return fetchAlerts(owner, repo, "code-scanning", pat);
            case DEPENDABOT:
                return fetchAlerts(owner, repo, "dependabot", pat);
            case SECRETSCAN:
                return fetchAlerts(owner, repo, "secret-scanning", pat);
            default:
                return "{}";
        }
    }

    private String fetchAlerts(String owner, String repo, String tool, String pat) throws JsonMappingException, JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, Object>> allAlerts = new ArrayList<>();

        int page = 1;
        int pageSize = 100; 
        boolean hasMore = true;

        while (hasMore) {
            final int currentPage = page;
            // Build the request for the current page
            String responseData = webClientBuilder.build()
                    .get()
                    .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("api.github.com")
                        .path("/repos/{owner}/{repo}/{tool}/alerts")
                        .queryParam("per_page", pageSize)
                        .queryParam("page", currentPage)
                        .build(owner, repo, tool)
                    )
                    .header("Authorization", "Bearer " + pat)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode root = mapper.readTree(responseData);
            if (!root.isArray()) {
                LOGGER.info("Warning: Expected array, got something else for page " + page);
                break;
            }

            int countThisPage = root.size();
            for (JsonNode alertNode : root) {
                Map<String, Object> alertMap = mapper.convertValue(
                        alertNode, new TypeReference<Map<String, Object>>() {});
                allAlerts.add(alertMap);
            }

            if (countThisPage < pageSize) {
                hasMore = false;
            } else {
                page++;
            }
        }

        return mapper.writeValueAsString(allAlerts);
    }
}