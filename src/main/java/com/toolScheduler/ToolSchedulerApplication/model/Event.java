package com.toolScheduler.ToolSchedulerApplication.model;

public interface Event<T> {
    String getEventId();
    EventTypes getType();
    T getPayload();
}
