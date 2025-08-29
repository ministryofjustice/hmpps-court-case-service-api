-- Are the data types correct in this script correct?

CREATE TABLE verdict (
    id INT PRIMARY KEY,
    verdict_date DATE,
    verdict_type TEXT,
    created_at TIMESTAMP,
    created_by TEXT,
    updated_at TIMESTAMP,
    updated_by TEXT,
    is_deleted BOOLEAN,
    version INT
);

CREATE TABLE plea (
    id INT PRIMARY KEY,
    plea_date DATE,
    plea_value TEXT,
    created_at TIMESTAMP,
    created_by TEXT,
    updated_at TIMESTAMP,
    updated_by TEXT,
    is_deleted BOOLEAN,
    version INT
);

CREATE TABLE offence (
    id INT PRIMARY KEY,
    verdict_id INT,
    plea_id INT,
    offence_code VARCHAR(20),
    offence_title TEXT,
    offence_act TEXT,
    list_number VARCHAR(20),
    sequence INT,
    facts TEXT,
    is_discontinued BOOLEAN,
    short_term_custody_predictor_score DECIMAL(10,2),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    updated_by TEXT,
    is_deleted BOOLEAN,
    version INT,
    FOREIGN KEY (verdict_id) REFERENCES verdict(id),
    FOREIGN KEY (plea_id) REFERENCES plea(id)
);

CREATE TABLE offence_summary (
    id INT PRIMARY KEY,
    offence_id INT,
    offence_code TEXT,
    order_index INT,
    offence_title TEXT,
    is_proceedings_concluded BOOLEAN,
    created_at TIMESTAMP,
    created_by TEXT,
    updated_at TIMESTAMP,
    updated_by TEXT,
    is_deleted BOOLEAN,
    version INT,
    FOREIGN KEY (offence_id) REFERENCES offence(id)
);

CREATE TABLE judicial_result (
    id INT PRIMARY KEY,
    offence_id INT,
    is_convicted_result BOOLEAN,
    label TEXT,
    judicial_result_type_id TEXT,
    judicial_result_text TEXT,
    is_deleted BOOLEAN,
    created_at TIMESTAMP,
    created_by TEXT,
    updated_at TIMESTAMP,
    updated_by TEXT,
    deleted BOOLEAN,
    version INT,
    FOREIGN KEY (offence_id) REFERENCES offence(id)
);

-- should this table contain only three columns ethnicity_id, ethnicity_code, and ethnicity_description?
-- then the person table contain two foreign keys? one for  observed_ethnicity_id and the other for self_defined_ethnicity_id?
CREATE TABLE ethnicity (
    id INT PRIMARY KEY,
    observed_ethnicity_id INT,
    observed_ethnicity_code TEXT,
    observed_ethnicity_description TEXT,
    self_defined_ethnicity_id INT,
    self_defined_ethnicity_code TEXT,
    self_defined_ethnicity_description TEXT,
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
    created_at TIMESTAMP,
    created_by TEXT,
    updated_at TIMESTAMP,
    updated_by TEXT,
    is_deleted BOOLEAN,
    version INT
);

CREATE TABLE address (
    id INT PRIMARY KEY,
    court_centre_id INT,
    address_1 TEXT,
    address_2 TEXT,
    address_3 TEXT,
    address_4 TEXT,
    address_5 TEXT,
    postcode TEXT,
    created_at TIMESTAMP,
    created_by TEXT,
    updated_at TIMESTAMP,
    updated_by TEXT,
    is_deleted BOOLEAN,
    version INT,
    FOREIGN KEY (court_centre_id) REFERENCES court_centre(id)
);

CREATE TABLE offender (
    id INT PRIMARY KEY,
    sitting_day TEXT,
    listing_sequence TEXT,
    listed_duration_minutes TEXT,
    is_cancelled TEXT,
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
    version INT
);

CREATE TABLE defendant (
    id INT PRIMARY KEY,
    offender_id INT,
    master_defendant_id TEXT,
    number_of_previous_convictions_cited TEXT,
    manual_update TEXT,
    mitigation TEXT,
    crn TEXT,
    cro_number TEXT,
    is_youth BOOLEAN,
    tsv_name TEXT,
    pnc_id TEXT,
    is_proceedings_concluded BOOLEAN,
    cpr_uuid TEXT,
    offender_confirmed TEXT,
    created_at TIMESTAMP,
    created_by TEXT,
    updated_at TIMESTAMP,
    updated_by TEXT,
    is_deleted BOOLEAN,
    version INT,
    FOREIGN KEY (offender_id) REFERENCES offender(id)
);

---- i've changed the names of this table to be more appropriate.
---- should numbers be varchar?
CREATE TABLE contact_information (
    id INT PRIMARY KEY,
    home_number TEXT,
    work_number TEXT,
    mobile_number TEXT,
    primary_email TEXT,
    secondary_email TEXT,
    fax_number TEXT,
    created_at TIMESTAMP,
    created_by TEXT,
    updated_at TIMESTAMP,
    updated_by TEXT,
    is_deleted BOOLEAN,
    version INT
);

CREATE TABLE person (
    id INT PRIMARY KEY,
    defendant_id INT,
    ethnicity_id INT,
    contact_information_id INT,
    address_id INT,
    title TEXT,
    first_name TEXT,
    middle_name TEXT,
    last_name TEXT,
    date_of_birth DATE,
    nationality_id TEXT,
    nationality_code TEXT,
    nationality_description TEXT,
    additional_nationality_id TEXT,
    additional_nationality_code TEXT,
    additional_nationality_description TEXT,
    disability_status TEXT,
    sex TEXT,
    national_insurance_number TEXT,
    occupation TEXT,
    occupation_code TEXT,
    created_at TIMESTAMP,
    created_by TEXT,
    updated_at TIMESTAMP,
    updated_by TEXT,
    is_deleted BOOLEAN,
    version INT,
    FOREIGN KEY (defendant_id) REFERENCES defendant(id),
    FOREIGN KEY (ethnicity_id) REFERENCES ethnicity(id),
    FOREIGN KEY (contact_information_id) REFERENCES contact_information(id),
    FOREIGN KEY (address_id) REFERENCES address(id)
);

CREATE TABLE prosecution_case (
    id INT PRIMARY KEY,
    case_urn TEXT,
    source_type TEXT,
    type_id TEXT,
    type_code TEXT,
    type_description TEXT,
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

CREATE TABLE prosecution_case_defendant (
    id INT PRIMARY KEY,
    defendant_id INT,
    prosecution_case_id INT,
    created_at TIMESTAMP,
    created_by TEXT,
    updated_at TIMESTAMP,
    updated_by TEXT,
    is_deleted BOOLEAN,
    version INT
);

CREATE TABLE case_document (
    id INT PRIMARY KEY,
    document_id INT,
    document_name TEXT,
    created_at TIMESTAMP,
    created_by TEXT,
    updated_at TIMESTAMP,
    updated_by TEXT,
    is_deleted BOOLEAN,
    version INT
);

CREATE TABLE prosecution_case_document (
    id INT PRIMARY KEY,
    prosecution_case_id INT,
    case_document_id INT,
    created_at TIMESTAMP,
    created_by TEXT,
    updated_at TIMESTAMP,
    updated_by TEXT,
    is_deleted BOOLEAN,
    version INT,
    FOREIGN KEY (prosecution_case_id) REFERENCES prosecution_case(id),
    FOREIGN KEY (case_document_id) REFERENCES case_document(id)
);

CREATE TABLE hearing (
    id INT PRIMARY KEY,
    type TEXT,
    event_type TEXT,
    list_number TEXT,
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
    hearing_id INT NOT NULL,
    court_centre_id INT NOT NULL,
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

CREATE TABLE hearing_case_note (
    id INT PRIMARY KEY,
    hearing_id INT,
    note TEXT,
    author TEXT,
    draft TEXT,
    legacy TEXT,
    created_by_uuid TEXT,
    created_at TIMESTAMP,
    created_by TEXT,
    updated_at TIMESTAMP,
    updated_by TEXT,
    is_deleted BOOLEAN,
    version INT,
    FOREIGN KEY (hearing_id) REFERENCES hearing(id)
);

CREATE TABLE hearing_outcome (
    id INT PRIMARY KEY,
    hearing_id INT,
    type TEXT,
    hearing_outcome_date TEXT,
    state TEXT,
    assigned_to TEXT,
    assigned_to_uuid TEXT,
    resulted_date TEXT,
    legacy TEXT,
    created_at TIMESTAMP,
    created_by TEXT,
    updated_at TIMESTAMP,
    updated_by TEXT,
    is_deleted BOOLEAN,
    version INT,
    FOREIGN KEY (hearing_id) REFERENCES hearing(id)
);

