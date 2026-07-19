package com.familylink.backend.mood;

import com.familylink.backend.family.Family;
import com.familylink.backend.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "mood_entries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MoodEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "family_id", nullable = false, updatable = false)
    private Family family;

    @Enumerated(EnumType.STRING)
    @Column(name = "mood_type", nullable = false, length = 20)
    private MoodType moodType;

    @Column(name = "note", length = 500)
    private String note;

    @Column(name = "visible_to_family", nullable = false)
    @Builder.Default
    private boolean visibleToFamily = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = OffsetDateTime.now();
    }
}
