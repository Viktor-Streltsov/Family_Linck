CREATE TABLE families (
                          id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          name VARCHAR(100) NOT NULL,
                          invite_code VARCHAR(20) NOT NULL UNIQUE,
                          created_by UUID NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
                          created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                          updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_families_invite_code ON families(invite_code);
CREATE INDEX idx_families_created_by ON families(created_by);

CREATE TABLE family_members (
                                id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                family_id UUID NOT NULL REFERENCES families(id) ON DELETE CASCADE,
                                user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                                role VARCHAR(20) NOT NULL,
                                joined_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                                CONSTRAINT uk_family_user UNIQUE (family_id, user_id),
                                CONSTRAINT chk_role CHECK (role IN ('PARENT', 'CHILD', 'GUARDIAN', 'OTHER'))
);

CREATE INDEX idx_family_members_user ON family_members(user_id);
CREATE INDEX idx_family_members_family ON family_members(family_id);