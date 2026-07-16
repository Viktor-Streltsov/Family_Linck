package com.familylink.backend.family.dto;

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
public class FamilyResponse {
    private UUID id;
    private String name;
    private String inviteCode;
    private UUID createdBy;
    private int membersCount;
    private OffsetDateTime createdAt;
}