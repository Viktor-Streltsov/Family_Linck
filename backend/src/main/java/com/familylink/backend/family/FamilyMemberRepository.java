package com.familylink.backend.family;

import com.familylink.backend.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FamilyMemberRepository extends JpaRepository<FamilyMember, UUID> {

    // Все семьи пользователя
    List<FamilyMember> findByUser(User user);

    // Все члены семьи
    List<FamilyMember> findByFamily(Family family);

    // Конкретное членство пользователя в семье
    Optional<FamilyMember> findByFamilyAndUser(Family family, User user);

    // Проверка: пользователь состоит в семье?
    boolean existsByFamilyAndUser(Family family, User user);
}