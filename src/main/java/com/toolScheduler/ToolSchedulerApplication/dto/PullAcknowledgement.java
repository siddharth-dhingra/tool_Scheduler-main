package com.toolScheduler.ToolSchedulerApplication.dto;

import java.util.UUID;

import com.toolScheduler.ToolSchedulerApplication.model.Acknowledgement;
import com.toolScheduler.ToolSchedulerApplication.model.AcknowledgementEvent;

public class PullAcknowledgement implements Acknowledgement<AcknowledgementEvent> {
    
    private String eventId;
    private AcknowledgementEvent payload;

    public PullAcknowledgement() {}

    public PullAcknowledgement(String eventId, AcknowledgementEvent payload) {
        this.payload = payload;
        this.eventId = (eventId == null || eventId.isEmpty()) ? UUID.randomUUID().toString() : eventId;
    }

    public void setPayload(AcknowledgementEvent payload) {
        this.payload = payload;
    }

    @Override
    public String getEventId() {
        return eventId;
    }

    @Override
    public AcknowledgementEvent getPayload() {
        return payload;
    }
}