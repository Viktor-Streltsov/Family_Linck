-- Ситуации (карточки проблем)
CREATE TABLE situations
(
    id           UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    family_id    UUID         NOT NULL REFERENCES families (id) ON DELETE CASCADE,
    created_by   UUID         NOT NULL REFERENCES users (id) ON DELETE RESTRICT,
    title        VARCHAR(200) NOT NULL,
    category     VARCHAR(30)  NOT NULL,
    status       VARCHAR(20)  NOT NULL DEFAULT 'OPEN',
    is_sensitive BOOLEAN      NOT NULL DEFAULT false,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    resolved_at  TIMESTAMPTZ,
    CONSTRAINT chk_situation_category CHECK (category IN (
                                                          'STUDY', 'DAILY_ROUTINE', 'GADGETS', 'EMOTIONS',
                                                          'CONFLICTS', 'FRIENDS', 'HEALTH', 'OTHER'
        )),
    CONSTRAINT chk_situation_status CHECK (status IN (
                                                      'OPEN', 'IN_DISCUSSION', 'RESOLVED', 'CLOSED'
        ))
);

CREATE INDEX idx_situations_family ON situations (family_id, created_at DESC);
CREATE INDEX idx_situations_status ON situations (family_id, status);

-- Участники ситуации со своим описанием
CREATE TABLE situation_participants
(
    id              UUID PRIMARY KEY     DEFAULT gen_random_uuid(),
    situation_id    UUID        NOT NULL REFERENCES situations (id) ON DELETE CASCADE,
    user_id         UUID        NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    description     TEXT,
    consented_to_ai BOOLEAN     NOT NULL DEFAULT false,
    consented_at    TIMESTAMPTZ,
    submitted_at    TIMESTAMPTZ,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_situation_user UNIQUE (situation_id, user_id)
);

CREATE INDEX idx_situation_participants_situation ON situation_participants (situation_id);
CREATE INDEX idx_situation_participants_user ON situation_participants (user_id);

-- AI-рекомендации по ситуации
CREATE TABLE ai_recommendations
(
    id            UUID PRIMARY KEY     DEFAULT gen_random_uuid(),
    situation_id  UUID        NOT NULL REFERENCES situations (id) ON DELETE CASCADE,
    status        VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    content       TEXT,
    resources     TEXT,
    model_version VARCHAR(50),
    safety_flag   BOOLEAN     NOT NULL DEFAULT false,
    generated_at  TIMESTAMPTZ,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_recommendation_status CHECK (status IN (
                                                           'PENDING', 'GENERATED', 'FAILED', 'BLOCKED_SENSITIVE'
        ))
);

CREATE INDEX idx_recommendations_situation ON ai_recommendations (situation_id);