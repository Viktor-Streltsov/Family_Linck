package com.familylink.backend.consent.controller;

import com.familylink.backend.consent.ConsentType;
import com.familylink.backend.consent.dto.ConsentRequest;
import com.familylink.backend.consent.dto.ConsentResponse;
import com.familylink.backend.consent.service.ConsentService;
import com.familylink.backend.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/consents")
@RequiredArgsConstructor
@Tag(name = "Согласия", description = "Управление согласиями на обработку данных")
@SecurityRequirement(name = "bearerAuth")
public class ConsentController {

    private final ConsentService consentService;

    @PostMapping
    @Operation(summary = "Дать согласие на обработку определённого типа данных")
    public ResponseEntity<ConsentResponse> grantConsent(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody ConsentRequest request) {
        ConsentResponse response = consentService.grantConsent(currentUser, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{type}")
    @Operation(summary = "Отозвать согласие определённого типа")
    public ResponseEntity<Void> revokeConsent(
            @AuthenticationPrincipal User currentUser,
            @PathVariable ConsentType type) {
        consentService.revokeConsent(currentUser, type);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my")
    @Operation(summary = "Мои согласия (история)")
    public ResponseEntity<List<ConsentResponse>> getMyConsents(
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(consentService.getMyConsents(currentUser));
    }
}