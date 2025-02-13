package com.toolScheduler.ToolSchedulerApplication.model;

public class UpdateEvent {

    private String tenantId;
    private ScanType toolType;
    private long alertNumber;
    private String newState;
    private String reason;

    public UpdateEvent() {}

    public UpdateEvent(String tenantId, ScanType toolType,
                       long alertNumber, String newState, String reason) {
        this.tenantId = tenantId;
        this.toolType = toolType;
        this.alertNumber = alertNumber;
        this.newState = newState;
        this.reason = reason;
    }

    public String getTenantId() {
        return tenantId;
    }
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public ScanType getToolType() {
        return toolType;
    }
    public void setToolType(ScanType toolType) {
        this.toolType = toolType;
    }

    public long getAlertNumber() {
        return alertNumber;
    }
    public void setAlertNumber(long alertNumber) {
        this.alertNumber = alertNumber;
    }

    public String getNewState() {
        return newState;
    }
    public void setNewState(String newState) {
        this.newState = newState;
    }

    public String getReason() {
        return reason;
    }
    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public String toString() {
        return "UpdateEvent{" +
                "tenantId='" + tenantId + '\'' +
                ", toolType=" + toolType +
                ", alertNumber=" + alertNumber +
                ", newState='" + newState + '\'' +
                ", reason='" + reason + '\'' +
                '}';
    }
}
