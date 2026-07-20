package com.familylink.backend.situation.dto;

import com.familylink.backend.situation.RecommendationStatus;
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
public class RecommendationResponse {
    private UUID id;
    private RecommendationStatus status;
    private String content;
    private String resources;
    private String modelVersion;
    private boolean safetyFlag;
    private OffsetDateTime generatedAt;
}