package com.toolScheduler.ToolSchedulerApplication.model;

import java.util.List;

public class ScanEvent {

    private String tenantId;
    private List<ScanType> types;

    public ScanEvent() {}

    public ScanEvent(String tenantId, List<ScanType> types) {
        this.tenantId = tenantId;
        this.types = types;
    }

    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }

    public List<ScanType> getTypes() { return types; }
    public void setTypes(List<ScanType> types) { this.types = types; }

    @Override
    public String toString() {
        return "ScanEvent{" +
                "tenantId='" + tenantId + '\'' +
                ", types=" + types +
                '}';
    }
}
