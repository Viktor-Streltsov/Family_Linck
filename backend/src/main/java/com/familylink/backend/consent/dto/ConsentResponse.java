package com.familylink.backend.consent.dto;

import com.familylink.backend.consent.ConsentType;
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
public class ConsentResponse {
    private UUID id;
    private ConsentType consentType;
    private boolean granted;
    private OffsetDateTime grantedAt;
    private OffsetDateTime revokedAt;
    private String consentVersion;
}