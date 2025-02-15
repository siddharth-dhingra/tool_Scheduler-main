package com.toolScheduler.ToolSchedulerApplication.dto;

import java.util.UUID;

import com.toolScheduler.ToolSchedulerApplication.model.Event;
import com.toolScheduler.ToolSchedulerApplication.model.EventTypes;
import com.toolScheduler.ToolSchedulerApplication.model.ScanEvent;

public class ScanRequestEvent implements Event<ScanEvent> {
    
    private String eventId;
    public static EventTypes TYPE = EventTypes.SCAN_PULL;
    private ScanEvent payload;

    public ScanRequestEvent(ScanEvent payload, String eventId) {
        this.payload = payload;
        this.eventId = (eventId == null || eventId.isEmpty()) ? UUID.randomUUID().toString() : eventId;
    }

    public ScanRequestEvent() {}

    public static EventTypes getTYPE() {
        return TYPE;
    }

    public static void setTYPE(EventTypes tYPE) {
        TYPE = tYPE;
    }

    public void setPayload(ScanEvent payload) {
        this.payload = payload;
    }

    @Override
    public EventTypes getType() {
        return TYPE;
    }

    @Override
    public ScanEvent getPayload() {
        return payload;
    }

    @Override
    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }
}