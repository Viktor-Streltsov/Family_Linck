package com.familylink.backend.mood.controller;

import com.familylink.backend.mood.dto.CreateMoodRequest;
import com.familylink.backend.mood.dto.MoodResponse;
import com.familylink.backend.mood.service.MoodService;
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
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Настроение", description = "Отслеживание эмоционального состояния членов семьи")
@SecurityRequirement(name = "bearerAuth")
public class MoodController {

    private final MoodService moodService;

    @PostMapping("/families/{familyId}/mood")
    @Operation(summary = "Отметить своё настроение в семье")
    public ResponseEntity<MoodResponse> createMood(
            @AuthenticationPrincipal User currentUser,
            @PathVariable UUID familyId,
            @Valid @RequestBody CreateMoodRequest request) {
        MoodResponse response = moodService.createMoodEntry(currentUser, familyId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/families/{familyId}/mood/today")
    @Operation(summary = "Настроения всех членов семьи за сегодня")
    public ResponseEntity<List<MoodResponse>> getTodayMoods(
            @AuthenticationPrincipal User currentUser,
            @PathVariable UUID familyId) {
        return ResponseEntity.ok(moodService.getTodayFamilyMoods(currentUser, familyId));
    }

    @GetMapping("/families/{familyId}/mood/my")
    @Operation(summary = "Моя история настроений в семье")
    public ResponseEntity<List<MoodResponse>> getMyMoodHistory(
            @AuthenticationPrincipal User currentUser,
            @PathVariable UUID familyId) {
        return ResponseEntity.ok(moodService.getMyMoodHistory(currentUser, familyId));
    }

    @DeleteMapping("/mood/my/all")
    @Operation(summary = "Удалить все свои эмоциональные данные (право на забвение)")
    public ResponseEntity<Map<String, Object>> deleteAllMyMoodData(
            @AuthenticationPrincipal User currentUser) {
        int deleted = moodService.deleteAllMyMoodData(currentUser);
        return ResponseEntity.ok(Map.of(
                "deletedCount", deleted,
                "message", "Все ваши записи о настроении удалены"
        ));
    }
}