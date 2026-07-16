package com.familylink.backend.family.service;

import com.familylink.backend.family.Family;
import com.familylink.backend.family.FamilyMember;
import com.familylink.backend.family.FamilyMemberRepository;
import com.familylink.backend.family.FamilyRepository;
import com.familylink.backend.family.dto.CreateFamilyRequest;
import com.familylink.backend.family.dto.FamilyMemberResponse;
import com.familylink.backend.family.dto.FamilyResponse;
import com.familylink.backend.family.dto.JoinFamilyRequest;
import com.familylink.backend.family.exception.AlreadyMemberException;
import com.familylink.backend.family.exception.FamilyNotFoundException;
import com.familylink.backend.family.exception.NotFamilyMemberException;
import com.familylink.backend.family.util.InviteCodeGenerator;
import com.familylink.backend.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FamilyService {

    private final FamilyRepository familyRepository;
    private final FamilyMemberRepository familyMemberRepository;
    private final InviteCodeGenerator inviteCodeGenerator;

    /**
     * Создать новую семью. Создатель автоматически становится её членом.
     */
    @Transactional
    public FamilyResponse createFamily(User creator, CreateFamilyRequest request) {
        String inviteCode = generateUniqueInviteCode();

        Family family = Family.builder()
                .name(request.getName().trim())
                .inviteCode(inviteCode)
                .createdBy(creator)
                .build();

        Family savedFamily = familyRepository.save(family);

        // Создатель автоматически становится членом семьи с выбранной ролью
        FamilyMember member = FamilyMember.builder()
                .family(savedFamily)
                .user(creator)
                .role(request.getCreatorRole())
                .build();

        familyMemberRepository.save(member);

        return toFamilyResponse(savedFamily, 1);
    }

    /**
     * Вступить в семью по invite-коду.
     */
    @Transactional
    public FamilyResponse joinFamily(User user, JoinFamilyRequest request) {
        Family family = familyRepository.findByInviteCode(request.getInviteCode())
                .orElseThrow(() -> new FamilyNotFoundException(
                        "Семья с кодом " + request.getInviteCode() + " не найдена"));

        if (familyMemberRepository.existsByFamilyAndUser(family, user)) {
            throw new AlreadyMemberException();
        }

        FamilyMember member = FamilyMember.builder()
                .family(family)
                .user(user)
                .role(request.getRole())
                .build();

        familyMemberRepository.save(member);

        int membersCount = familyMemberRepository.findByFamily(family).size();
        return toFamilyResponse(family, membersCount);
    }

    /**
     * Получить все семьи пользователя.
     */
    @Transactional(readOnly = true)
    public List<FamilyResponse> getMyFamilies(User user) {
        return familyMemberRepository.findByUser(user).stream()
                .map(FamilyMember::getFamily)
                .map(family -> toFamilyResponse(
                        family,
                        familyMemberRepository.findByFamily(family).size()))
                .toList();
    }

    /**
     * Получить участников семьи. Только для тех, кто в ней состоит.
     */
    @Transactional(readOnly = true)
    public List<FamilyMemberResponse> getFamilyMembers(User currentUser, UUID familyId) {
        Family family = familyRepository.findById(familyId)
                .orElseThrow(() -> new FamilyNotFoundException("Семья не найдена"));

        // Проверка: текущий пользователь состоит в этой семье?
        if (!familyMemberRepository.existsByFamilyAndUser(family, currentUser)) {
            throw new NotFamilyMemberException();
        }

        return familyMemberRepository.findByFamily(family).stream()
                .map(this::toFamilyMemberResponse)
                .toList();
    }

    // --- private helpers ---

    private String generateUniqueInviteCode() {
        // На случай крайне маловероятной коллизии — до 5 попыток
        for (int i = 0; i < 5; i++) {
            String code = inviteCodeGenerator.generate();
            if (!familyRepository.existsByInviteCode(code)) {
                return code;
            }
        }
        throw new IllegalStateException("Не удалось сгенерировать уникальный invite-код");
    }

    private FamilyResponse toFamilyResponse(Family family, int membersCount) {
        return FamilyResponse.builder()
                .id(family.getId())
                .name(family.getName())
                .inviteCode(family.getInviteCode())
                .createdBy(family.getCreatedBy().getId())
                .membersCount(membersCount)
                .createdAt(family.getCreatedAt())
                .build();
    }

    private FamilyMemberResponse toFamilyMemberResponse(FamilyMember member) {
        User u = member.getUser();
        return FamilyMemberResponse.builder()
                .memberId(member.getId())
                .userId(u.getId())
                .userName(u.getName())
                .userEmail(u.getEmail())
                .avatarUrl(u.getAvatarUrl())
                .role(member.getRole())
                .joinedAt(member.getJoinedAt())
                .build();
    }
}