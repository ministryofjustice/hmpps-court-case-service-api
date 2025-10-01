CREATE TABLE offence (
    id INT PRIMARY KEY,
    code VARCHAR(20),
    title TEXT,
    legislation TEXT,
    listing_number INT,
    sequence INT,
    short_term_custody_predictor_score DECIMAL(10,2),
    wording TEXT,
    verdict JSONB,
    plea JSONB,
    judicial_results JSONB,
    created_at TIMESTAMP,
    created_by TEXT,
    updated_at TIMESTAMP,
    updated_by TEXT,
    is_deleted BOOLEAN,
    version INT
);

CREATE TABLE defendant (
    id INT PRIMARY KEY,
    master_defendant_id INT,
    is_manual_update BOOLEAN,
    crn TEXT,
    cro_number TEXT,
    is_youth BOOLEAN,
    tsv_name TEXT,
    pnc_id INT,
    is_proceedings_concluded BOOLEAN,
    cpr_uuid TEXT,
    is_offender_confirmed BOOLEAN,
    person JSONB,
    sex JSONB,
    created_at TIMESTAMP,
    created_by TEXT,
    updated_at TIMESTAMP,
    updated_by TEXT,
    is_deleted BOOLEAN,
    version INT
);

CREATE TABLE defendant_offence (
    id INT PRIMARY KEY,
    offence_id INT NOT NULL,
    defendant_id INT NOT NULL,
    created_at TIMESTAMP,
    created_by TEXT,
    updated_at TIMESTAMP,
    updated_by TEXT,
    is_deleted BOOLEAN,
    version INT,
    FOREIGN KEY (offence_id) REFERENCES offence(id),
    FOREIGN KEY (defendant_id) REFERENCES defendant(id)
);

CREATE TABLE offender (
    id INT PRIMARY KEY,
    defendant_id INT NOT NULL,
    suspended_sentence_order TEXT,
    breach TEXT,
    awaiting_psr TEXT,
    probation_status TEXT,
    pre_sentence_activity TEXT,
    previously_known_termination_date TEXT,
    created_at TIMESTAMP,
    created_by TEXT,
    updated_at TIMESTAMP,
    updated_by TEXT,
    is_deleted BOOLEAN,
    version INT,
    FOREIGN KEY (defendant_id) REFERENCES defendant(id)
);

CREATE TABLE prosecution_case (
    id INT PRIMARY KEY,
    case_urn TEXT,
    source_type TEXT,
    c_id INT,
    case_markers JSONB,
    case_documents JSONB,
    created_at TIMESTAMP,
    created_by TEXT,
    updated_at TIMESTAMP,
    updated_by TEXT,
    is_deleted BOOLEAN,
    version INT
);

CREATE TABLE offender_match_group (
    id INT PRIMARY KEY,
    defendant_id INT,
    prosecution_case_id INT,
    created_at TIMESTAMP,
    created_by TEXT,
    updated_at TIMESTAMP,
    updated_by TEXT,
    is_deleted BOOLEAN,
    version INT,
    FOREIGN KEY (defendant_id) REFERENCES defendant(id),
    FOREIGN KEY (prosecution_case_id) REFERENCES prosecution_case(id)
);

CREATE TABLE offender_match (
    id INT PRIMARY KEY,
    offender_id INT,
    offender_match_group_id INT,
    created_at TIMESTAMP,
    created_by TEXT,
    updated_at TIMESTAMP,
    updated_by TEXT,
    is_deleted BOOLEAN,
    version INT,
    FOREIGN KEY (offender_id) REFERENCES offender(id),
    FOREIGN KEY (offender_match_group_id) REFERENCES offender_match_group(id)
);

CREATE TABLE hearing (
    id INT PRIMARY KEY,
    type TEXT,
    event_type TEXT,
    list_number TEXT,
    first_created TIMESTAMP,
    hearing_outcome JSONB,
    hearing_case_note JSONB,
    created_at TIMESTAMP,
    created_by TEXT,
    updated_at TIMESTAMP,
    updated_by TEXT,
    is_deleted BOOLEAN,
    version INT
);

CREATE TABLE court_centre (
    id INT PRIMARY KEY,
    code TEXT,
    name TEXT,
    room_id INT,
    room_name TEXT,
    psa_code TEXT,
    region TEXT,
    address JSONB,
    created_at TIMESTAMP,
    created_by TEXT,
    updated_at TIMESTAMP,
    updated_by TEXT,
    is_deleted BOOLEAN,
    version INT
);

CREATE TABLE hearing_prosecution_case (
    id INT PRIMARY KEY,
    hearing_id INT,
    prosecution_case_id INT,
    created_at TIMESTAMP,
    created_by TEXT,
    updated_at TIMESTAMP,
    updated_by TEXT,
    is_deleted BOOLEAN,
    version INT,
    FOREIGN KEY (hearing_id) REFERENCES hearing(id),
    FOREIGN KEY (prosecution_case_id) REFERENCES prosecution_case(id)
);

CREATE TABLE hearing_defendant (
    id INT PRIMARY KEY,
    defendant_id INT NOT NULL,
    hearing_id INT NOT NULL,
    created_at TIMESTAMP,
    created_by TEXT,
    updated_at TIMESTAMP,
    updated_by TEXT,
    is_deleted BOOLEAN,
    version INT,
    FOREIGN KEY (defendant_id) REFERENCES defendant(id),
    FOREIGN KEY (hearing_id) REFERENCES hearing(id)
);

CREATE TABLE hearing_day (
    id INT PRIMARY KEY,
    court_centre_id INT NOT NULL,
    hearing_id INT NOT NULL,
    sitting_day TEXT,
    hearing_day_time TEXT,
    listed_duration_minutes TEXT,
    is_cancelled TEXT,
    created_at TIMESTAMP,
    created_by TEXT,
    updated_at TIMESTAMP,
    updated_by TEXT,
    is_deleted BOOLEAN,
    version INT,
    FOREIGN KEY (hearing_id) REFERENCES hearing(id),
    FOREIGN KEY (court_centre_id) REFERENCES court_centre(id)
);

CREATE TABLE prosecution_case_defendant (
    id INT PRIMARY KEY,
    defendant_id INT,
    prosecution_case_id INT,
    created_at TIMESTAMP,
    created_by TEXT,
    updated_at TIMESTAMP,
    updated_by TEXT,
    is_deleted BOOLEAN,
    version INT,
    FOREIGN KEY (defendant_id) REFERENCES defendant(id),
    FOREIGN KEY (prosecution_case_id) REFERENCES prosecution_case(id)
);