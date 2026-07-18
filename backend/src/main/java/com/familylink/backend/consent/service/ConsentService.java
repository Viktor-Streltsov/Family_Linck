package com.familylink.backend.consent.service;

import com.familylink.backend.consent.ConsentType;
import com.familylink.backend.consent.UserConsent;
import com.familylink.backend.consent.UserConsentRepository;
import com.familylink.backend.consent.dto.ConsentRequest;
import com.familylink.backend.consent.dto.ConsentResponse;
import com.familylink.backend.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConsentService {

    private final UserConsentRepository consentRepository;

    private static final String CURRENT_VERSION = "v1.0";

    @Transactional
    public ConsentResponse grantConsent(User user, ConsentRequest request) {
        // Если было активное согласие того же типа — отзываем перед выдачей нового
        consentRepository
                .findByUserAndConsentTypeAndRevokedAtIsNull(user, request.getConsentType())
                .ifPresent(existing -> {
                    existing.setRevokedAt(OffsetDateTime.now());
                    consentRepository.save(existing);
                });

        UserConsent consent = UserConsent.builder()
                .user(user)
                .consentType(request.getConsentType())
                .granted(request.isGranted())
                .grantedAt(OffsetDateTime.now())
                .consentVersion(CURRENT_VERSION)
                .build();

        UserConsent saved = consentRepository.save(consent);
        return toResponse(saved);
    }

    @Transactional
    public void revokeConsent(User user, ConsentType type) {
        consentRepository
                .findByUserAndConsentTypeAndRevokedAtIsNull(user, type)
                .ifPresent(existing -> {
                    existing.setRevokedAt(OffsetDateTime.now());
                    consentRepository.save(existing);
                });
    }

    @Transactional(readOnly = true)
    public List<ConsentResponse> getMyConsents(User user) {
        return consentRepository.findAll().stream()
                .filter(c -> c.getUser().getId().equals(user.getId()))
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public boolean hasActiveConsent(User user, ConsentType type) {
        return consentRepository
                .existsByUserAndConsentTypeAndGrantedTrueAndRevokedAtIsNull(user, type);
    }

    private ConsentResponse toResponse(UserConsent c) {
        return ConsentResponse.builder()
                .id(c.getId())
                .consentType(c.getConsentType())
                .granted(c.isGranted())
                .grantedAt(c.getGrantedAt())
                .revokedAt(c.getRevokedAt())
                .consentVersion(c.getConsentVersion())
                .build();
    }
}