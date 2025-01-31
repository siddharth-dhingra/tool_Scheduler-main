package com.toolScheduler.ToolSchedulerApplication.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.toolScheduler.ToolSchedulerApplication.model.ScanEvent;
import com.toolScheduler.ToolSchedulerApplication.model.ScanType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class GitHubScanService {

    private final WebClient.Builder webClientBuilder;

    public GitHubScanService(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    public String performSingleToolScan(String pat, ScanEvent event, ScanType toolType) throws JsonMappingException, JsonProcessingException {
        switch (toolType) {
            case CODE_SCAN:
                return fetchCodeScanningAlerts(event.getOwner(), event.getRepo(), pat);
            case DEPENDABOT:
                return fetchDependabotAlerts(event.getOwner(), event.getRepo(), pat);
            case SECRET_SCAN:
                return fetchSecretScanningAlerts(event.getOwner(), event.getRepo(), pat);
            default:
                // If we ever get "ALL" or something else, fallback to empty
                return "{}";
        }
    }

    private String fetchCodeScanningAlerts(String owner, String repo, String pat) throws JsonMappingException, JsonProcessingException {
        // return webClientBuilder.build()
        //         .get()
        //         .uri("https://api.github.com/repos/{owner}/{repo}/code-scanning/alerts", owner, repo)
        //         .header("Authorization", "Bearer " + pat)
        //         .retrieve()
        //         .bodyToMono(String.class)
        //         .onErrorReturn("[]")
        //         .block();

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
                        .path("/repos/{owner}/{repo}/code-scanning/alerts")
                        .queryParam("per_page", pageSize)
                        .queryParam("page", currentPage)
                        .build(owner, repo)
                    )
                    .header("Authorization", "Bearer " + pat)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            // Parse the page's JSON
            JsonNode root = mapper.readTree(responseData);
            if (!root.isArray()) {
                System.out.println("Warning: Expected array, got something else for page " + page);
                break;
            }

            int countThisPage = root.size();
            // Convert each alert to a Map and add to the master list
            for (JsonNode alertNode : root) {
                Map<String, Object> alertMap = mapper.convertValue(
                        alertNode, new TypeReference<Map<String, Object>>() {});
                allAlerts.add(alertMap);
            }

            // If fewer than pageSize alerts returned, we've reached the last page
            if (countThisPage < pageSize) {
                hasMore = false;
            } else {
                page++;
            }
        }

        // Convert the collected alerts into one unified JSON array
        return mapper.writeValueAsString(allAlerts);
    }

    private String fetchDependabotAlerts(String owner, String repo, String pat) throws JsonMappingException, JsonProcessingException {
        // return webClientBuilder.build()
        //         .get()
        //         .uri("https://api.github.com/repos/{owner}/{repo}/dependabot/alerts", owner, repo)
        //         .header("Authorization", "Bearer " + pat)
        //         .retrieve()
        //         .bodyToMono(String.class)
        //         .onErrorReturn("[]")
        //         .block();

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
                        .path("/repos/{owner}/{repo}/dependabot/alerts")
                        .queryParam("per_page", pageSize)
                        .queryParam("page", currentPage)
                        .build(owner, repo)
                    )
                    .header("Authorization", "Bearer " + pat)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            // Parse the page's JSON
            JsonNode root = mapper.readTree(responseData);
            if (!root.isArray()) {
                System.out.println("Warning: Expected array, got something else for page " + page);
                break;
            }

            int countThisPage = root.size();
            // Convert each alert to a Map and add to the master list
            for (JsonNode alertNode : root) {
                Map<String, Object> alertMap = mapper.convertValue(
                        alertNode, new TypeReference<Map<String, Object>>() {});
                allAlerts.add(alertMap);
            }

            // If fewer than pageSize alerts returned, we've reached the last page
            if (countThisPage < pageSize) {
                hasMore = false;
            } else {
                page++;
            }
        }

        // Convert the collected alerts into one unified JSON array
        return mapper.writeValueAsString(allAlerts);
    }
    private String fetchSecretScanningAlerts(String owner, String repo, String pat) throws JsonMappingException, JsonProcessingException {
        // return webClientBuilder.build()
        //         .get()
        //         .uri("https://api.github.com/repos/{owner}/{repo}/secret-scanning/alerts", owner, repo)
        //         .header("Authorization", "Bearer " + pat)
        //         .retrieve()
        //         .bodyToMono(String.class)
        //         .onErrorReturn("[]")
        //         .block();

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
                        .path("/repos/{owner}/{repo}/secret-scanning/alerts")
                        .queryParam("per_page", pageSize)
                        .queryParam("page", currentPage)
                        .build(owner, repo)
                    )
                    .header("Authorization", "Bearer " + pat)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            // Parse the page's JSON
            JsonNode root = mapper.readTree(responseData);
            if (!root.isArray()) {
                System.out.println("Warning: Expected array, got something else for page " + page);
                break;
            }

            int countThisPage = root.size();
            // Convert each alert to a Map and add to the master list
            for (JsonNode alertNode : root) {
                Map<String, Object> alertMap = mapper.convertValue(
                        alertNode, new TypeReference<Map<String, Object>>() {});
                allAlerts.add(alertMap);
            }

            // If fewer than pageSize alerts returned, we've reached the last page
            if (countThisPage < pageSize) {
                hasMore = false;
            } else {
                page++;
            }
        }

        // Convert the collected alerts into one unified JSON array
        return mapper.writeValueAsString(allAlerts);
    }
}