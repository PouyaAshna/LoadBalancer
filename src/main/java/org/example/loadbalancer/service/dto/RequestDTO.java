package org.example.loadbalancer.service.dto;

import java.time.Instant;
import java.util.UUID;

public class RequestDTO {

    private final String id;
    private final Instant createdAt;

    public RequestDTO() {
        id = UUID.randomUUID().toString();
        createdAt = Instant.now();
    }

    public String getId() {
        return id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
