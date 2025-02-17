package com.toolScheduler.ToolSchedulerApplication.model;

public class FileLocationEvent {

    private String tenantId;   
    private String filePath;
    private ToolType toolName;
    private String destinationTopic;

    public FileLocationEvent() {}

    public FileLocationEvent(String tenantId, String filePath, ToolType toolName) {
        this.tenantId = tenantId;
        this.filePath = filePath;
        this.toolName = toolName;
    }

    public String getTenantId() {
        return tenantId;
    }
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
    
    public String getFilePath() {
        return filePath;
    }
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    
    public ToolType getToolName() {
        return toolName;
    }
    public void setToolName(ToolType toolName) {
        this.toolName = toolName;
    }

    public String getDestinationTopic() {
        return destinationTopic;
    }
    public void setDestinationTopic(String destinationTopic) {
        this.destinationTopic = destinationTopic;
    }

    @Override
    public String toString() {
        return "FileLocationEvent{" +
                "tenantId='" + tenantId + '\'' +
                ", filePath='" + filePath + '\'' +
                ", toolName='" + toolName + '\'' +
                ", destinationTopic='" + destinationTopic + '\'' +
                '}';
    }
}
