package com.familylink.backend.situation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SituationParticipantResponse {
    private UUID id;
    private UUID userId;
    private String userName;
    private String description;    // видно только владельцу или всем участникам ситуации
    private boolean consentedToAi;
    private boolean hasSubmitted;
    private OffsetDateTime submittedAt;
}