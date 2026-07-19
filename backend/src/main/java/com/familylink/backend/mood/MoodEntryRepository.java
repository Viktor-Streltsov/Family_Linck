package com.familylink.backend.mood;

import com.familylink.backend.family.Family;
import com.familylink.backend.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface MoodEntryRepository extends JpaRepository<MoodEntry, UUID> {

    // Настроения в семье за период (только видимые семье)
    @Query("""
        SELECT m FROM MoodEntry m
        WHERE m.family = :family
        AND m.visibleToFamily = true
        AND m.createdAt >= :from
        AND m.createdAt < :to
        ORDER BY m.createdAt DESC
        """)
    List<MoodEntry> findByFamilyAndCreatedAtBetween(
            @Param("family") Family family,
            @Param("from") OffsetDateTime from,
            @Param("to") OffsetDateTime to
    );

    // Мои настроения в семье
    @Query("""
        SELECT m FROM MoodEntry m
        WHERE m.user = :user
        AND m.family = :family
        ORDER BY m.createdAt DESC
        """)
    List<MoodEntry> findByUserAndFamily(
            @Param("user") User user,
            @Param("family") Family family
    );

    // Все мои настроения (для удаления данных)
    List<MoodEntry> findByUser(User user);

    // Удаление старых записей (для автоочистки)
    long deleteByCreatedAtBefore(OffsetDateTime cutoff);
}