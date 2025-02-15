package com.toolScheduler.ToolSchedulerApplication.model;

public class FileLocationEvent {

    private String tenantId;   
    private String filePath;
    private ToolType toolName;

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

    @Override
    public String toString() {
        return "FileLocationEvent{" +
                "tenantId='" + tenantId + '\'' +
                ", filePath='" + filePath + '\'' +
                ", toolName='" + toolName + '\'' +
                '}';
    }
}
