package com.toolScheduler.ToolSchedulerApplication.model;


public class ScanEvent {
    private String tenantId;
    private ToolType toolType;

    public ScanEvent() {
    }

    public ScanEvent(String tenantId, ToolType toolType) {
        this.tenantId = tenantId;
        this.toolType = toolType;
    }

    public String getTenantId() {
        return tenantId;
    }
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public ToolType getToolType() {
        return toolType;
    }
    public void setToolType(ToolType toolType) {
        this.toolType = toolType;
    }

    @Override
    public String toString() {
        return "ScanEvent{" +
                "tenantId='" + tenantId + '\'' +
                ", toolType=" + toolType +
                '}';
    }
}
