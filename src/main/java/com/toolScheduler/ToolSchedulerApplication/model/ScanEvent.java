package com.toolScheduler.ToolSchedulerApplication.model;


import java.util.List;

public class ScanEvent {
    private String repo;
    private String owner;
    private List<ScanType> types;

    public ScanEvent() {
    }

    public ScanEvent(String repo, String owner, List<ScanType> types) {
        this.repo = repo;
        this.owner = owner;
        this.types = types;
    }

    public String getRepo() {
        return repo;
    }

    public void setRepo(String repo) {
        this.repo = repo;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public List<ScanType> getTypes() {
        return types;
    }

    public void setTypes(List<ScanType> types) {
        this.types = types;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ScanEvent)) return false;

        ScanEvent scanEvent = (ScanEvent) o;
        if (repo != null ? !repo.equals(scanEvent.repo) : scanEvent.repo != null) return false;
        if (owner != null ? !owner.equals(scanEvent.owner) : scanEvent.owner != null) return false;
        if (types != null ? !types.equals(scanEvent.types) : scanEvent.types != null) return false;
        return false;
    }

    @Override
    public int hashCode() {
        int result = repo != null ? repo.hashCode() : 0;
        result = 31 * result + (owner != null ? owner.hashCode() : 0);
        result = 31 * result + (types != null ? types.hashCode() : 0);
        return result;
    }

    // toString for debugging/logging
    @Override
    public String toString() {
        return "ScanEvent{" +
                "repo='" + repo + '\'' +
                ", owner='" + owner + '\'' +
                ", types=" + types +
                '}';
    }
    }
