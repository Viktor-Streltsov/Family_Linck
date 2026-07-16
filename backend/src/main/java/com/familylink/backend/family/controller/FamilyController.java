package com.familylink.backend.family.controller;

import com.familylink.backend.family.dto.CreateFamilyRequest;
import com.familylink.backend.family.dto.FamilyMemberResponse;
import com.familylink.backend.family.dto.FamilyResponse;
import com.familylink.backend.family.dto.JoinFamilyRequest;
import com.familylink.backend.family.service.FamilyService;
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
@RequestMapping("/api/families")
@RequiredArgsConstructor
@Tag(name = "Семьи", description = "Создание, вступление и управление семьями")
@SecurityRequirement(name = "bearerAuth")
public class FamilyController {

    private final FamilyService familyService;

    @PostMapping
    @Operation(summary = "Создать новую семью")
    public ResponseEntity<FamilyResponse> createFamily(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody CreateFamilyRequest request) {
        FamilyResponse response = familyService.createFamily(currentUser, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/join")
    @Operation(summary = "Вступить в семью по invite-коду")
    public ResponseEntity<FamilyResponse> joinFamily(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody JoinFamilyRequest request) {
        FamilyResponse response = familyService.joinFamily(currentUser, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my")
    @Operation(summary = "Получить список моих семей")
    public ResponseEntity<List<FamilyResponse>> getMyFamilies(
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(familyService.getMyFamilies(currentUser));
    }

    @GetMapping("/{familyId}/members")
    @Operation(summary = "Получить участников семьи")
    public ResponseEntity<List<FamilyMemberResponse>> getFamilyMembers(
            @AuthenticationPrincipal User currentUser,
            @PathVariable UUID familyId) {
        return ResponseEntity.ok(familyService.getFamilyMembers(currentUser, familyId));
    }
}