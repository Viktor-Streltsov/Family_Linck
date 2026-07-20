package com.familylink.backend.situation.controller;

import com.familylink.backend.situation.dto.*;
import com.familylink.backend.situation.service.SituationService;
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
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Ситуации", description = "Обсуждение семейных ситуаций с AI-помощником")
@SecurityRequirement(name = "bearerAuth")
public class SituationController {

    private final SituationService situationService;

    @PostMapping("/families/{familyId}/situations")
    @Operation(summary = "Создать новую ситуацию в семье")
    public ResponseEntity<SituationResponse> createSituation(
            @AuthenticationPrincipal User currentUser,
            @PathVariable UUID familyId,
            @Valid @RequestBody CreateSituationRequest request) {
        SituationResponse response = situationService.createSituation(currentUser, familyId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/families/{familyId}/situations")
    @Operation(summary = "Список ситуаций в семье")
    public ResponseEntity<List<SituationResponse>> getFamilySituations(
            @AuthenticationPrincipal User currentUser,
            @PathVariable UUID familyId) {
        return ResponseEntity.ok(situationService.getFamilySituations(currentUser, familyId));
    }

    @GetMapping("/situations/{situationId}")
    @Operation(summary = "Получить ситуацию по id")
    public ResponseEntity<SituationResponse> getSituation(
            @AuthenticationPrincipal User currentUser,
            @PathVariable UUID situationId) {
        return ResponseEntity.ok(situationService.getSituation(currentUser, situationId));
    }

    @PostMapping("/situations/{situationId}/join")
    @Operation(summary = "Присоединиться к ситуации как участник")
    public ResponseEntity<SituationResponse> joinSituation(
            @AuthenticationPrincipal User currentUser,
            @PathVariable UUID situationId) {
        return ResponseEntity.ok(situationService.joinSituation(currentUser, situationId));
    }

    @PostMapping("/situations/{situationId}/description")
    @Operation(summary = "Добавить своё описание ситуации")
    public ResponseEntity<SituationResponse> submitDescription(
            @AuthenticationPrincipal User currentUser,
            @PathVariable UUID situationId,
            @Valid @RequestBody SubmitDescriptionRequest request) {
        return ResponseEntity.ok(situationService.submitDescription(currentUser, situationId, request));
    }

    @PostMapping("/situations/{situationId}/recommendation")
    @Operation(summary = "Получить AI-рекомендацию по ситуации")
    public ResponseEntity<RecommendationResponse> getRecommendation(
            @AuthenticationPrincipal User currentUser,
            @PathVariable UUID situationId) {
        return ResponseEntity.ok(situationService.requestRecommendation(currentUser, situationId));
    }
}