package com.toolScheduler.ToolSchedulerApplication.dto;

import java.util.UUID;

import com.toolScheduler.ToolSchedulerApplication.model.Acknowledgement;
import com.toolScheduler.ToolSchedulerApplication.model.AcknowledgementPayload;

public class PullAcknowledgement implements Acknowledgement<AcknowledgementPayload> {
    
    private String eventId;
    private AcknowledgementPayload payload;

    public PullAcknowledgement() {}

    public PullAcknowledgement(String eventId, AcknowledgementPayload payload) {
        this.payload = payload;
        this.eventId = (eventId == null || eventId.isEmpty()) ? UUID.randomUUID().toString() : eventId;
    }

    public void setPayload(AcknowledgementPayload payload) {
        this.payload = payload;
    }

    @Override
    public String getEventId() {
        return eventId;
    }

    @Override
    public AcknowledgementPayload getPayload() {
        return payload;
    }
}