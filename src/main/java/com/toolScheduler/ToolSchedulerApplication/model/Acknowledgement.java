package com.toolScheduler.ToolSchedulerApplication.model;

public interface Acknowledgement<T> {
    String getEventId();
    T getPayload();
}
