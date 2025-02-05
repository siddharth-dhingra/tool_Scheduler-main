package com.toolScheduler.ToolSchedulerApplication.model;

public class UpdateEvent {

    private String owner;       
    private String repo;        
    private ScanType toolType;    
    private long alertNumber;   
    private String newState;    
    private String reason;      

    public UpdateEvent() {}

    public UpdateEvent(String owner, String repo, ScanType toolType,
                       long alertNumber, String newState, String reason) {
        this.owner = owner;
        this.repo = repo;
        this.toolType = toolType;
        this.alertNumber = alertNumber;
        this.newState = newState;
        this.reason = reason;
    }

    // Getters and setters

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getRepo() {
        return repo;
    }

    public void setRepo(String repo) {
        this.repo = repo;
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
}