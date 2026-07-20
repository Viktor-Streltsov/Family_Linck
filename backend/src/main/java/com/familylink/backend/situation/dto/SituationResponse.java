package com.familylink.backend.situation.dto;

import com.familylink.backend.situation.SituationCategory;
import com.familylink.backend.situation.SituationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SituationResponse {
    private UUID id;
    private UUID familyId;
    private UUID createdBy;
    private String createdByName;
    private String title;
    private SituationCategory category;
    private SituationStatus status;
    private boolean sensitive;
    private int participantsCount;
    private int submittedDescriptionsCount;
    private boolean hasRecommendation;
    private OffsetDateTime createdAt;
    private OffsetDateTime resolvedAt;
    private List<SituationParticipantResponse> participants;
}