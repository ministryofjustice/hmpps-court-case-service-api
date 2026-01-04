CREATE TABLE IF NOT EXISTS offence(
    id UUID DEFAULT uuidv7() PRIMARY KEY,
    legacy_id INT,
    code VARCHAR(20),
    title TEXT NOT NULL,
    legislation TEXT,
    listing_number INT,
    wording TEXT NOT NULL,
    sequence INT NOT NULL,
    short_term_custody_predictor_score NUMERIC(21,19),
    verdict JSONB,
    plea JSONB,
    judicial_result JSONB,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by TEXT,
    updated_at TIMESTAMPTZ,
    updated_by TEXT,
    is_soft_deleted BOOLEAN DEFAULT false NOT NULL,
    version INT DEFAULT 0 NOT NULL
);

CREATE TABLE IF NOT EXISTS offender(
    id UUID DEFAULT uuidv7() PRIMARY KEY,
    legacy_id INT,
    crn TEXT UNIQUE,
    cro TEXT,
    pnc TEXT,
    is_suspended_sentence_order BOOLEAN DEFAULT false NOT NULL,
    is_breach BOOLEAN DEFAULT false NOT NULL,
    is_awaiting_psr BOOLEAN,
    is_pre_sentence_activity BOOLEAN DEFAULT false NOT NULL,
    previously_known_termination_date DATE,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by TEXT,
    updated_at TIMESTAMPTZ,
    updated_by TEXT,
    is_soft_deleted BOOLEAN DEFAULT false NOT NULL,
    version INT DEFAULT 0 NOT NULL
);

CREATE TABLE IF NOT EXISTS prosecution_case(
    id UUID DEFAULT uuidv7() PRIMARY KEY,
    legacy_id INT,
    case_id TEXT,
    case_number TEXT,
    case_urn JSONB,
    source_type TEXT,
    case_marker JSONB,
    case_document JSONB,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by TEXT,
    updated_at TIMESTAMPTZ,
    updated_by TEXT,
    is_soft_deleted BOOLEAN DEFAULT false NOT NULL,
    version INT DEFAULT 0 NOT NULL
);

CREATE TABLE IF NOT EXISTS hearing(
    id UUID DEFAULT uuidv7() PRIMARY KEY,
    legacy_id INT,
    hearing_id UUID,
    type TEXT,
    event_type TEXT,
    list_number TEXT,
    prep_status TEXT,
    is_hearing_outcome_not_required BOOLEAN,
    first_created TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    hearing_outcome JSONB,
    hearing_case_note JSONB,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by TEXT,
    updated_at TIMESTAMPTZ,
    updated_by TEXT,
    is_soft_deleted BOOLEAN DEFAULT false NOT NULL,
    version INT DEFAULT 0 NOT NULL
);

CREATE TABLE IF NOT EXISTS court_centre(
    id UUID DEFAULT uuidv7() PRIMARY KEY,
    legacy_id INT,
    code TEXT NOT NULL,
    name TEXT NOT NULL,
    court_room JSONB,
    psa_code TEXT,
    region TEXT,
    address JSONB,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by TEXT,
    updated_at TIMESTAMPTZ,
    updated_by TEXT,
    is_soft_deleted BOOLEAN DEFAULT false NOT NULL,
    version INT DEFAULT 0 NOT NULL
);

CREATE TABLE IF NOT EXISTS defendant(
    id UUID DEFAULT uuidv7() PRIMARY KEY,
    offender_id UUID,
    legacy_id INT,
    defendant_id UUID UNIQUE NOT NULL,
    master_defendant_id UUID,
    type TEXT DEFAULT 'PERSON'::text NOT NULL,
    cpr_uuid UUID,
    c_id TEXT,
    pnc TEXT,
    cro TEXT,
    crn TEXT,
    person JSONB NOT NULL,
    address JSONB,
    is_youth BOOLEAN,
    tsv_name TSVECTOR,
    is_proceedings_concluded BOOLEAN,
    is_offender_confirmed BOOLEAN DEFAULT false NOT NULL,
    is_manual_update BOOLEAN DEFAULT false NOT NULL,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by TEXT,
    updated_at TIMESTAMPTZ,
    updated_by TEXT,
    is_soft_deleted BOOLEAN DEFAULT false NOT NULL,
    version INT DEFAULT 0 NOT NULL,
    FOREIGN KEY (offender_id) REFERENCES offender(id) ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS offender_match_group(
    id UUID DEFAULT uuidv7() PRIMARY KEY,
    defendant_id UUID,
    prosecution_case_id UUID,
    legacy_id INT,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by TEXT,
    updated_at TIMESTAMPTZ,
    updated_by TEXT,
    is_soft_deleted BOOLEAN DEFAULT false NOT NULL,
    version INT DEFAULT 0 NOT NULL,
    FOREIGN KEY (defendant_id) REFERENCES defendant(id) ON UPDATE CASCADE,
    FOREIGN KEY (prosecution_case_id) REFERENCES prosecution_case(id) ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS offender_match(
    id UUID DEFAULT uuidv7() PRIMARY KEY,
    offender_id UUID,
    offender_match_group_id UUID,
    legacy_id INT,
    match_type VARCHAR(255),
    aliases JSONB,
    is_rejected BOOLEAN NOT NULL,
    match_probability FLOAT8,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by TEXT,
    updated_at TIMESTAMPTZ,
    updated_by TEXT,
    is_soft_deleted BOOLEAN DEFAULT false NOT NULL,
    version INT DEFAULT 0 NOT NULL,
    FOREIGN KEY (offender_id) REFERENCES offender(id) ON UPDATE CASCADE,
    FOREIGN KEY (offender_match_group_id) REFERENCES offender_match_group(id) ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS case_comments(
    id UUID DEFAULT uuidv7() PRIMARY KEY,
    defendant_id UUID NOT NULL,
    prosecution_case_id UUID NOT NULL,
    legacy_id INT,
    author TEXT NOT NULL,
    comment TEXT NOT NULL,
    is_draft BOOLEAN DEFAULT false,
    is_legacy BOOLEAN DEFAULT false NOT NULL,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by TEXT,
    created_by_uuid UUID,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_by TEXT,
    is_soft_deleted BOOLEAN DEFAULT false NOT NULL,
    version INT DEFAULT 0 NOT NULL,
    FOREIGN KEY (defendant_id) REFERENCES defendant(id) ON UPDATE CASCADE,
    FOREIGN KEY (prosecution_case_id) REFERENCES prosecution_case(id) ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS hearing_day(
    id UUID DEFAULT uuidv7() PRIMARY KEY,
    court_centre_id UUID NOT NULL,
    hearing_id UUID NOT NULL,
    sitting_day DATE NOT NULL,
    sitting_time TIMETZ NOT NULL,
    listed_duration_minutes TIMETZ,
    is_cancelled BOOLEAN,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by TEXT,
    updated_at TIMESTAMPTZ,
    updated_by TEXT,
    is_soft_deleted BOOLEAN DEFAULT false NOT NULL,
    version INT DEFAULT 0 NOT NULL,
    FOREIGN KEY (court_centre_id) REFERENCES court_centre(id) ON UPDATE CASCADE,
    FOREIGN KEY (hearing_id) REFERENCES hearing(id) ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS defendant_offence(
    id UUID DEFAULT uuidv7() PRIMARY KEY,
    offence_id UUID,
    defendant_id UUID,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by TEXT,
    updated_at TIMESTAMPTZ,
    updated_by TEXT,
    is_soft_deleted BOOLEAN DEFAULT false NOT NULL,
    version INT DEFAULT 0 NOT NULL,
    FOREIGN KEY (offence_id) REFERENCES offence(id) ON UPDATE CASCADE,
    FOREIGN KEY (defendant_id) REFERENCES defendant(id) ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS defendant_prosecution_case(
    id UUID DEFAULT uuidv7() PRIMARY KEY,
    defendant_id UUID,
    prosecution_case_id UUID,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by TEXT,
    updated_at TIMESTAMPTZ,
    updated_by TEXT,
    is_soft_deleted BOOLEAN DEFAULT false NOT NULL,
    version INT DEFAULT 0 NOT NULL,
    FOREIGN KEY (defendant_id) REFERENCES defendant(id) ON UPDATE CASCADE,
    FOREIGN KEY (prosecution_case_id) REFERENCES prosecution_case(id) ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS defendant_hearing(
    id UUID DEFAULT uuidv7() PRIMARY KEY,
    defendant_id UUID,
    hearing_id UUID,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by TEXT,
    updated_at TIMESTAMPTZ,
    updated_by TEXT,
    is_soft_deleted BOOLEAN DEFAULT false NOT NULL,
    version INT DEFAULT 0 NOT NULL,
    FOREIGN KEY (defendant_id) REFERENCES defendant(id) ON UPDATE CASCADE,
    FOREIGN KEY (hearing_id) REFERENCES hearing(id) ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS prosecution_case_hearing(
    id UUID DEFAULT uuidv7() PRIMARY KEY,
    prosecution_case_id UUID,
    hearing_id UUID,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by TEXT,
    updated_at TIMESTAMPTZ,
    updated_by TEXT,
    is_soft_deleted BOOLEAN DEFAULT false NOT NULL,
    version INT DEFAULT 0 NOT NULL,
    FOREIGN KEY (prosecution_case_id) REFERENCES prosecution_case(id) ON UPDATE CASCADE,
    FOREIGN KEY (hearing_id) REFERENCES hearing(id) ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS failed_messages(
    id UUID DEFAULT uuidv7() PRIMARY KEY,
    message_id UUID,
    correlation_id UUID,
    error_message TEXT NOT NULL,
    original_message JSONB NOT NULL,
    source_type TEXT NOT NULL,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by TEXT,
    updated_at TIMESTAMPTZ,
    updated_by TEXT,
    is_soft_deleted BOOLEAN DEFAULT false NOT NULL,
    version INT DEFAULT 0 NOT NULL
);