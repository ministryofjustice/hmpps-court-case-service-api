ALTER TABLE defendant DROP CONSTRAINT IF EXISTS defendant_pkey CASCADE;
ALTER TABLE defendant DROP COLUMN id CASCADE;
ALTER TABLE defendant ADD COLUMN id UUID PRIMARY KEY;
ALTER TABLE defendant ADD COLUMN legacy_id BIGINT;
ALTER TABLE defendant ADD COLUMN defendant_id UUID;
ALTER TABLE defendant DROP COLUMN offender_id CASCADE;
ALTER TABLE defendant ADD COLUMN offender_id UUID;
ALTER TABLE defendant ADD COLUMN legacy_offender_id BIGINT;
CREATE INDEX idx_defendant_legacy_offender_id ON defendant (legacy_offender_id);

ALTER TABLE defendant_offence DROP CONSTRAINT IF EXISTS defendant_offence_pkey CASCADE;
ALTER TABLE defendant_offence DROP COLUMN id CASCADE;
ALTER TABLE defendant_offence ADD COLUMN id UUID PRIMARY KEY;
ALTER TABLE defendant_offence DROP COLUMN defendant_id CASCADE;
ALTER TABLE defendant_offence ADD COLUMN defendant_id UUID;
ALTER TABLE defendant_offence ADD COLUMN legacy_defendant_id BIGINT;
CREATE INDEX idx_defendant_offence_legacy_defendant_id ON defendant_offence (legacy_defendant_id);
ALTER TABLE defendant_offence DROP COLUMN offence_id CASCADE;
ALTER TABLE defendant_offence ADD COLUMN offence_id UUID;
ALTER TABLE defendant_offence ADD COLUMN legacy_offence_id BIGINT;
CREATE INDEX idx_defendant_offence_legacy_offence_id ON defendant_offence (legacy_offence_id);

ALTER TABLE offence DROP CONSTRAINT IF EXISTS offence_pkey CASCADE;
ALTER TABLE offence DROP COLUMN id CASCADE;
ALTER TABLE offence ADD COLUMN id UUID PRIMARY KEY;
ALTER TABLE offence ADD COLUMN legacy_id BIGINT;

ALTER TABLE offender DROP CONSTRAINT IF EXISTS offender_pkey CASCADE;
ALTER TABLE offender DROP COLUMN id CASCADE;
ALTER TABLE offender ADD COLUMN id UUID PRIMARY KEY;
ALTER TABLE offender ADD COLUMN legacy_id BIGINT;

ALTER TABLE offender_match DROP CONSTRAINT IF EXISTS offender_match_pkey CASCADE;
ALTER TABLE offender_match DROP COLUMN id CASCADE;
ALTER TABLE offender_match ADD COLUMN id UUID PRIMARY KEY;
ALTER TABLE offender_match ADD COLUMN legacy_id BIGINT;
ALTER TABLE offender_match DROP COLUMN offender_id CASCADE;
ALTER TABLE offender_match ADD COLUMN offender_id UUID;
ALTER TABLE offender_match ADD COLUMN legacy_offender_id BIGINT;
CREATE INDEX idx_offender_match_legacy_offender_id ON offender_match (legacy_offender_id);
ALTER TABLE offender_match DROP COLUMN offender_match_group_id CASCADE;
ALTER TABLE offender_match ADD COLUMN offender_match_group_id UUID;
ALTER TABLE offender_match ADD COLUMN legacy_offender_match_group_id BIGINT;
CREATE INDEX idx_offender_match_legacy_offender_match_group_id ON offender_match (legacy_offender_match_group_id);

ALTER TABLE offender_match_group DROP CONSTRAINT IF EXISTS offender_match_group_pkey CASCADE;
ALTER TABLE offender_match_group DROP COLUMN id CASCADE;
ALTER TABLE offender_match_group ADD COLUMN id UUID PRIMARY KEY;
ALTER TABLE offender_match_group ADD COLUMN legacy_id BIGINT;
ALTER TABLE offender_match_group DROP COLUMN defendant_id CASCADE;
ALTER TABLE offender_match_group ADD COLUMN defendant_id UUID;
ALTER TABLE offender_match_group ADD COLUMN legacy_defendant_id BIGINT;
CREATE INDEX idx_offender_match_group_legacy_defendant_id ON offender_match_group (legacy_defendant_id);
ALTER TABLE offender_match_group DROP COLUMN prosecution_case_id CASCADE;
ALTER TABLE offender_match_group ADD COLUMN prosecution_case_id UUID;
ALTER TABLE offender_match_group ADD COLUMN legacy_prosecution_case_id BIGINT;
CREATE INDEX idx_offender_match_group_legacy_prosecution_case_id ON offender_match_group (legacy_prosecution_case_id);

ALTER TABLE court_centre DROP CONSTRAINT IF EXISTS court_centre_pkey CASCADE;
ALTER TABLE court_centre DROP COLUMN id CASCADE;
ALTER TABLE court_centre ADD COLUMN id UUID PRIMARY KEY;
ALTER TABLE court_centre ADD COLUMN legacy_id BIGINT;

ALTER TABLE hearing DROP CONSTRAINT IF EXISTS hearing_pkey CASCADE;
ALTER TABLE hearing DROP COLUMN id CASCADE;
ALTER TABLE hearing ADD COLUMN id UUID PRIMARY KEY;
ALTER TABLE hearing ADD COLUMN legacy_id BIGINT;

ALTER TABLE prosecution_case DROP CONSTRAINT IF EXISTS prosecution_case_pkey CASCADE;
ALTER TABLE prosecution_case DROP COLUMN id CASCADE;
ALTER TABLE prosecution_case ADD COLUMN id UUID PRIMARY KEY;
ALTER TABLE prosecution_case ADD COLUMN legacy_id BIGINT;

