package com.familylink.backend.family.dto;

import com.familylink.backend.family.FamilyRole;
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
public class FamilyMemberResponse {
    private UUID memberId;
    private UUID userId;
    private String userName;
    private String userEmail;
    private String avatarUrl;
    private FamilyRole role;
    private OffsetDateTime joinedAt;
}