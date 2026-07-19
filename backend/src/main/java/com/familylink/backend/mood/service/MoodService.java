package com.familylink.backend.mood.service;

import com.familylink.backend.consent.ConsentType;
import com.familylink.backend.consent.exception.ConsentRequiredException;
import com.familylink.backend.consent.service.ConsentService;
import com.familylink.backend.family.Family;
import com.familylink.backend.family.FamilyMemberRepository;
import com.familylink.backend.family.FamilyRepository;
import com.familylink.backend.family.exception.FamilyNotFoundException;
import com.familylink.backend.family.exception.NotFamilyMemberException;
import com.familylink.backend.mood.MoodEntry;
import com.familylink.backend.mood.MoodEntryRepository;
import com.familylink.backend.mood.dto.CreateMoodRequest;
import com.familylink.backend.mood.dto.MoodResponse;
import com.familylink.backend.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MoodService {

    private final MoodEntryRepository moodRepository;
    private final FamilyRepository familyRepository;
    private final FamilyMemberRepository familyMemberRepository;
    private final ConsentService consentService;

    @Transactional
    public MoodResponse createMoodEntry(User user, UUID familyId, CreateMoodRequest request) {
        // 1. Проверка согласия на трекинг настроения
        if (!consentService.hasActiveConsent(user, ConsentType.MOOD_TRACKING)) {
            throw new ConsentRequiredException(ConsentType.MOOD_TRACKING);
        }

        // 2. Проверка, что семья существует
        Family family = familyRepository.findById(familyId)
                .orElseThrow(() -> new FamilyNotFoundException("Семья не найдена"));

        // 3. Проверка, что пользователь в этой семье
        if (!familyMemberRepository.existsByFamilyAndUser(family, user)) {
            throw new NotFamilyMemberException();
        }

        // 4. Создание записи
        MoodEntry entry = MoodEntry.builder()
                .user(user)
                .family(family)
                .moodType(request.getMoodType())
                .note(request.getNote() != null ? request.getNote().trim() : null)
                .visibleToFamily(request.isVisibleToFamily())
                .build();

        MoodEntry saved = moodRepository.save(entry);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<MoodResponse> getTodayFamilyMoods(User user, UUID familyId) {
        Family family = getValidatedFamily(user, familyId);

        // Начало и конец сегодняшнего дня в UTC
        OffsetDateTime startOfDay = LocalDate.now().atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime endOfDay = startOfDay.plusDays(1);

        return moodRepository.findByFamilyAndCreatedAtBetween(family, startOfDay, endOfDay)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MoodResponse> getMyMoodHistory(User user, UUID familyId) {
        Family family = getValidatedFamily(user, familyId);
        return moodRepository.findByUserAndFamily(user, family)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Right to be forgotten — удалить все свои эмоциональные данные.
     */
    @Transactional
    public int deleteAllMyMoodData(User user) {
        List<MoodEntry> entries = moodRepository.findByUser(user);
        moodRepository.deleteAll(entries);
        return entries.size();
    }

    // --- private ---

    private Family getValidatedFamily(User user, UUID familyId) {
        Family family = familyRepository.findById(familyId)
                .orElseThrow(() -> new FamilyNotFoundException("Семья не найдена"));
        if (!familyMemberRepository.existsByFamilyAndUser(family, user)) {
            throw new NotFamilyMemberException();
        }
        return family;
    }

    private MoodResponse toResponse(MoodEntry entry) {
        User u = entry.getUser();
        return MoodResponse.builder()
                .id(entry.getId())
                .userId(u.getId())
                .userName(u.getName())
                .userAvatar(u.getAvatarUrl())
                .moodType(entry.getMoodType())
                .note(entry.getNote())
                .visibleToFamily(entry.isVisibleToFamily())
                .createdAt(entry.getCreatedAt())
                .build();
    }
}