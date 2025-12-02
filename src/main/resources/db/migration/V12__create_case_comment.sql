CREATE TABLE case_comment (
    id UUID PRIMARY KEY,
    legacy_id BIGINT,
    defendant_id UUID,
    legacy_defendant_id UUID,
    case_id UUID,
    legacy_case_id TEXT,
    author TEXT,
    comment TEXT,
    is_draft BOOLEAN,
    is_legacy BOOLEAN,
    created_at TIMESTAMP,
    created_by TEXT,
    updated_at TIMESTAMP,
    updated_by TEXT,
    is_deleted BOOLEAN,
    version INT
);