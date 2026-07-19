package com.familylink.backend.mood.dto;

import com.familylink.backend.mood.MoodType;
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
public class MoodResponse {
    private UUID id;
    private UUID userId;
    private String userName;
    private String userAvatar;
    private MoodType moodType;
    private String note;
    private boolean visibleToFamily;
    private OffsetDateTime createdAt;
}