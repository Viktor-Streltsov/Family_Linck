CREATE TABLE user_consents (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    consent_type VARCHAR(50) NOT NULL,
    granted BOOLEAN NOT NULL,
    granted_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    revoked_at TIMESTAMPTZ,
    consent_version VARCHAR(20) NOT NULL DEFAULT 'v1.0',
    CONSTRAINT chk_consent_type CHECK (consent_type IN (
            'MOOD_TRACKING',
            'AI_ANALYSIS',
            'DATA_SHARING_WITH_FAMILY'
        ))
);

CREATE INDEX idx_user_consents_user ON user_consents(user_id);
CREATE INDEX idx_user_consents_type ON user_consents(user_id, consent_type);

CREATE TABLE mood_entries (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    family_id UUID NOT NULL REFERENCES families(id) ON DELETE CASCADE,
    mood_type VARCHAR(20) NOT NULL,
    note VARCHAR(500),
    visible_to_family BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_mood_type CHECK(mood_type IN (
            'GREAT',
            'GOOD',
            'NEUTRAL',
            'TIRED',
            'SAD',
            'ANGRY',
            'ANXIOUS',
        ))
)

CREATE INDEX idx_mood_entries_user ON mood_entries(user_id);
CREATE INDEX idx_mood_entries_family ON mood_entries(family_id);
CREATE INDEX idx_mood_entries_created ON mood_entries(created_at);
CREATE INDEX idx_mood_entries_family_created ON mood_entries(family_id, created_at DESC);