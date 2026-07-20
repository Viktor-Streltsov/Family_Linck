package com.familylink.backend.situation.service;

import com.familylink.backend.consent.ConsentType;
import com.familylink.backend.consent.exception.ConsentRequiredException;
import com.familylink.backend.consent.service.ConsentService;
import com.familylink.backend.family.Family;
import com.familylink.backend.family.FamilyMemberRepository;
import com.familylink.backend.family.FamilyRepository;
import com.familylink.backend.family.exception.FamilyNotFoundException;
import com.familylink.backend.family.exception.NotFamilyMemberException;
import com.familylink.backend.situation.*;
import com.familylink.backend.situation.ai.AiAnalysisResult;
import com.familylink.backend.situation.ai.AiAnalysisService;
import com.familylink.backend.situation.dto.*;
import com.familylink.backend.situation.exception.*;
import com.familylink.backend.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SituationService {

    private final SituationRepository situationRepository;
    private final SituationParticipantRepository participantRepository;
    private final AiRecommendationRepository recommendationRepository;
    private final FamilyRepository familyRepository;
    private final FamilyMemberRepository familyMemberRepository;
    private final ConsentService consentService;
    private final AiAnalysisService aiAnalysisService;

    /**
     * Создать ситуацию. Создатель автоматически становится её участником.
     */
    @Transactional
    public SituationResponse createSituation(User user, UUID familyId, CreateSituationRequest request) {
        Family family = getValidatedFamily(user, familyId);

        Situation situation = Situation.builder()
                .family(family)
                .createdBy(user)
                .title(request.getTitle().trim())
                .category(request.getCategory())
                .build();

        Situation saved = situationRepository.save(situation);

        // Создатель — первый участник
        SituationParticipant creator = SituationParticipant.builder()
                .situation(saved)
                .user(user)
                .build();
        participantRepository.save(creator);

        return toResponse(saved, user);
    }

    /**
     * Присоединиться к ситуации как участник.
     */
    @Transactional
    public SituationResponse joinSituation(User user, UUID situationId) {
        Situation situation = situationRepository.findById(situationId)
                .orElseThrow(SituationNotFoundException::new);

        // Проверка: пользователь в этой семье
        if (!familyMemberRepository.existsByFamilyAndUser(situation.getFamily(), user)) {
            throw new NotFamilyMemberException();
        }

        // Если уже участник — просто возвращаем ситуацию
        if (participantRepository.findBySituationAndUser(situation, user).isEmpty()) {
            SituationParticipant participant = SituationParticipant.builder()
                    .situation(situation)
                    .user(user)
                    .build();
            participantRepository.save(participant);
        }

        return toResponse(situation, user);
    }

    /**
     * Добавить или обновить своё описание ситуации.
     */
    @Transactional
    public SituationResponse submitDescription(User user, UUID situationId, SubmitDescriptionRequest request) {
        Situation situation = situationRepository.findById(situationId)
                .orElseThrow(SituationNotFoundException::new);

        SituationParticipant participant = participantRepository.findBySituationAndUser(situation, user)
                .orElseThrow(NotSituationParticipantException::new);

        // Если участник даёт согласие на AI — нужно активное согласие AI_ANALYSIS
        if (request.isConsentToAiAnalysis()
                && !consentService.hasActiveConsent(user, ConsentType.AI_ANALYSIS)) {
            throw new ConsentRequiredException(ConsentType.AI_ANALYSIS);
        }

        participant.setDescription(request.getDescription().trim());
        participant.setConsentedToAi(request.isConsentToAiAnalysis());
        if (request.isConsentToAiAnalysis()) {
            participant.setConsentedAt(OffsetDateTime.now());
        }
        participant.setSubmittedAt(OffsetDateTime.now());
        participantRepository.save(participant);

        // Автоматически меняем статус на IN_DISCUSSION, если все участники описали
        List<SituationParticipant> all = participantRepository.findBySituation(situation);
        boolean allSubmitted = !all.isEmpty()
                && all.stream().allMatch(p -> p.getSubmittedAt() != null);
        if (allSubmitted && situation.getStatus() == SituationStatus.OPEN) {
            situation.setStatus(SituationStatus.IN_DISCUSSION);
            situationRepository.save(situation);
        }

        return toResponse(situation, user);
    }

    /**
     * Запросить AI-рекомендацию по ситуации.
     * Требуется минимум 2 участника с согласием на AI.
     */
    @Transactional
    public RecommendationResponse requestRecommendation(User user, UUID situationId) {
        Situation situation = situationRepository.findById(situationId)
                .orElseThrow(SituationNotFoundException::new);

        // Только участник ситуации может запросить рекомендацию
        if (participantRepository.findBySituationAndUser(situation, user).isEmpty()) {
            throw new NotSituationParticipantException();
        }

        // Собираем участников с согласием на AI и с описанием
        List<SituationParticipant> consentedParticipants = participantRepository
                .findBySituation(situation).stream()
                .filter(p -> p.isConsentedToAi() && p.getDescription() != null)
                .toList();

        // Проверка 1: минимум 2 участника с согласием
        if (consentedParticipants.size() < 2) {
            throw new RecommendationNotReadyException(
                    "Нужно минимум 2 участника с описаниями и согласием на AI-анализ. " +
                            "Сейчас: " + consentedParticipants.size());
        }

        // Проверка 2: участники должны быть РАЗНЫМИ пользователями
        long uniqueUsers = consentedParticipants.stream()
                .map(p -> p.getUser().getId())
                .distinct()
                .count();
        if (uniqueUsers < 2) {
            throw new SelfDialogueException();
        }

        // Проверка 3: у участников должны быть РАЗНЫЕ роли в семье
        long uniqueRoles = consentedParticipants.stream()
                .map(p -> familyMemberRepository
                        .findByFamilyAndUser(situation.getFamily(), p.getUser())
                        .map(fm -> fm.getRole())
                        .orElse(null))
                .filter(Objects::nonNull)
                .distinct()
                .count();
        if (uniqueRoles < 2) {
            throw new SameRoleParticipantsException();
        }

        // Если уже есть готовая рекомендация — возвращаем её
        return recommendationRepository.findBySituation(situation)
                .filter(r -> r.getStatus() == RecommendationStatus.GENERATED
                        || r.getStatus() == RecommendationStatus.BLOCKED_SENSITIVE)
                .map(this::toRecommendationResponse)
                .orElseGet(() -> generateAndSaveRecommendation(situation));
    }

    /**
     * Получить список ситуаций в семье.
     */
    @Transactional(readOnly = true)
    public List<SituationResponse> getFamilySituations(User user, UUID familyId) {
        Family family = getValidatedFamily(user, familyId);
        return situationRepository.findByFamilyOrderByCreatedAtDesc(family).stream()
                .map(s -> toResponse(s, user))
                .toList();
    }

    /**
     * Получить одну ситуацию.
     */
    @Transactional(readOnly = true)
    public SituationResponse getSituation(User user, UUID situationId) {
        Situation situation = situationRepository.findById(situationId)
                .orElseThrow(SituationNotFoundException::new);

        // Доступ только участникам семьи
        if (!familyMemberRepository.existsByFamilyAndUser(situation.getFamily(), user)) {
            throw new NotFamilyMemberException();
        }

        return toResponse(situation, user);
    }

    // --- private ---

    private RecommendationResponse generateAndSaveRecommendation(Situation situation) {
        AiAnalysisResult result = aiAnalysisService.generateRecommendation(situation);

        AiRecommendation recommendation = AiRecommendation.builder()
                .situation(situation)
                .status(result.safetyFlag()
                        ? RecommendationStatus.BLOCKED_SENSITIVE
                        : (result.success()
                        ? RecommendationStatus.GENERATED
                        : RecommendationStatus.FAILED))
                .content(result.content())
                .resources(result.resources())
                .modelVersion(result.modelVersion())
                .safetyFlag(result.safetyFlag())
                .generatedAt(OffsetDateTime.now())
                .build();

        // Если safety-flag — помечаем ситуацию как sensitive
        if (result.safetyFlag()) {
            situation.setSensitive(true);
            situationRepository.save(situation);
        }

        AiRecommendation saved = recommendationRepository.save(recommendation);
        return toRecommendationResponse(saved);
    }

    private Family getValidatedFamily(User user, UUID familyId) {
        Family family = familyRepository.findById(familyId)
                .orElseThrow(() -> new FamilyNotFoundException("Семья не найдена"));
        if (!familyMemberRepository.existsByFamilyAndUser(family, user)) {
            throw new NotFamilyMemberException();
        }
        return family;
    }

    private SituationResponse toResponse(Situation s, User currentUser) {
        List<SituationParticipant> participants = participantRepository.findBySituation(s);

        List<SituationParticipantResponse> participantDtos = participants.stream()
                .map(p -> SituationParticipantResponse.builder()
                        .id(p.getId())
                        .userId(p.getUser().getId())
                        .userName(p.getUser().getName())
                        // Своё описание видит сам, чужие — только если участник ситуации
                        .description(p.getUser().getId().equals(currentUser.getId())
                                ? p.getDescription()
                                : (p.getSubmittedAt() != null ? p.getDescription() : null))
                        .consentedToAi(p.isConsentedToAi())
                        .hasSubmitted(p.getSubmittedAt() != null)
                        .submittedAt(p.getSubmittedAt())
                        .build())
                .toList();

        long submitted = participants.stream()
                .filter(p -> p.getSubmittedAt() != null)
                .count();

        boolean hasRec = recommendationRepository.findBySituation(s).isPresent();

        return SituationResponse.builder()
                .id(s.getId())
                .familyId(s.getFamily().getId())
                .createdBy(s.getCreatedBy().getId())
                .createdByName(s.getCreatedBy().getName())
                .title(s.getTitle())
                .category(s.getCategory())
                .status(s.getStatus())
                .sensitive(s.isSensitive())
                .participantsCount(participants.size())
                .submittedDescriptionsCount((int) submitted)
                .hasRecommendation(hasRec)
                .createdAt(s.getCreatedAt())
                .resolvedAt(s.getResolvedAt())
                .participants(participantDtos)
                .build();
    }

    private RecommendationResponse toRecommendationResponse(AiRecommendation r) {
        return RecommendationResponse.builder()
                .id(r.getId())
                .status(r.getStatus())
                .content(r.getContent())
                .resources(r.getResources())
                .modelVersion(r.getModelVersion())
                .safetyFlag(r.isSafetyFlag())
                .generatedAt(r.getGeneratedAt())
                .build();
    }
}