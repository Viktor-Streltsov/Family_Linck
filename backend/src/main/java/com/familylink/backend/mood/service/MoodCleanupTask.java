package com.familylink.backend.mood.service;

import com.familylink.backend.mood.MoodEntryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class MoodCleanupTask {

    private final MoodEntryRepository moodRepository;

    private static final int RETENTION_MONTHS = 6;

    /**
     * Раз в сутки в 3:00 по серверному времени удаляем записи старше 6 месяцев.
     * Cron: секунды минуты часы день-месяца месяц день-недели
     */
    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void cleanupOldMoodEntries() {
        OffsetDateTime cutoff = OffsetDateTime.now().minusMonths(RETENTION_MONTHS);
        long deleted = moodRepository.deleteByCreatedAtBefore(cutoff);
        log.info("Mood cleanup: deleted {} entries older than {}", deleted, cutoff);
    }
}